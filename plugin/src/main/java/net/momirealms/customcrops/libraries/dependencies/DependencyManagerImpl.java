/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customcrops.libraries.dependencies;

import com.google.common.collect.ImmutableSet;
import net.momirealms.customcrops.CustomCropsPluginImpl;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.libraries.classpath.ClassPathAppender;
import net.momirealms.customcrops.libraries.dependencies.classloader.IsolatedClassLoader;
import net.momirealms.customcrops.libraries.dependencies.relocation.Relocation;
import net.momirealms.customcrops.libraries.dependencies.relocation.RelocationHandler;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public class DependencyManagerImpl implements DependencyManager {

    /** A registry containing plugin specific behaviour for dependencies. */
    private final DependencyRegistry registry;
    /** The path where library jars are cached. */
    private final Path cacheDirectory;
    /** The classpath appender to preload dependencies into */
    private final ClassPathAppender classPathAppender;
    /** A map of dependencies which have already been loaded. */
    private final EnumMap<Dependency, Path> loaded = new EnumMap<>(Dependency.class);
    /** A map of isolated classloaders which have been created. */
    private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    /** Cached relocation handler instance. */
    private @MonotonicNonNull RelocationHandler relocationHandler = null;

    public DependencyManagerImpl(CustomCropsPluginImpl plugin, ClassPathAppender classPathAppender) {
        this.registry = new DependencyRegistry();
        this.cacheDirectory = setupCacheDirectory(plugin);
        this.classPathAppender = classPathAppender;
        this.relocationHandler = new RelocationHandler(this);
    }

    @Override
    public ClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
        ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

        for (Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            URL[] urls = set.stream()
                    .map(this.loaded::get)
                    .map(file -> {
                        try {
                            return file.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    @Override
    public void loadDependencies(Collection<Dependency> dependencies) {
        CountDownLatch latch = new CountDownLatch(dependencies.size());

        for (Dependency dependency : dependencies) {
            if (this.loaded.containsKey(dependency)) {
                latch.countDown();
                continue;
            }

            try {
                loadDependency(dependency);
            } catch (Throwable e) {
                new RuntimeException("Unable to load dependency " + dependency.name(), e).printStackTrace();
            } finally {
                latch.countDown();
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void loadDependency(Dependency dependency) throws Exception {
        if (this.loaded.containsKey(dependency)) {
            return;
        }

        Path file = remapDependency(dependency, downloadDependency(dependency));

        this.loaded.put(dependency, file);

        if (this.classPathAppender != null && this.registry.shouldAutoLoad(dependency)) {
            this.classPathAppender.addJarToClasspath(file);
        }
    }

    private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
        Path file = this.cacheDirectory.resolve(dependency.getFileName(null));

        // if the file already exists, don't attempt to re-download it.
        if (Files.exists(file)) {
            return file;
        }

        DependencyDownloadException lastError = null;
        String fileName = dependency.getFileName(null);
        String forceRepo = dependency.getRepo();
        if (forceRepo == null) {
            // attempt to download the dependency from each repo in order.
            for (DependencyRepository repo : DependencyRepository.values()) {
                if (repo.getId().equals("maven") && TimeZone.getDefault().getID().startsWith("Asia")) {
                    continue;
                }
                try {
                    LogUtils.info("Downloading dependency(" + fileName + ") from " + repo.getUrl() + dependency.getMavenRepoPath());
                    repo.download(dependency, file);
                    LogUtils.info("Successfully downloaded " + fileName);
                    return file;
                } catch (DependencyDownloadException e) {
                    lastError = e;
                }
            }
        } else {
            DependencyRepository repository = DependencyRepository.getByID(forceRepo);
            if (repository != null) {
                try {
                    LogUtils.info("Downloading dependency(" + fileName + ") from " + repository.getUrl() + dependency.getMavenRepoPath());
                    repository.download(dependency, file);
                    LogUtils.info("Successfully downloaded " + fileName);
                    return file;
                } catch (DependencyDownloadException e) {
                    lastError = e;
                }
            }
        }
        throw Objects.requireNonNull(lastError);
    }

    private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
        List<Relocation> rules = new ArrayList<>(dependency.getRelocations());
        if (rules.isEmpty()) {
            return normalFile;
        }

        Path remappedFile = this.cacheDirectory.resolve(dependency.getFileName(DependencyRegistry.isGsonRelocated() ? "remapped-legacy" : "remapped"));

        // if the remapped source exists already, just use that.
        if (Files.exists(remappedFile)) {
            return remappedFile;
        }

        LogUtils.info("Remapping " + dependency.getFileName(null));
        relocationHandler.remap(normalFile, remappedFile, rules);
        LogUtils.info("Successfully remapped " + dependency.getFileName(null));
        return remappedFile;
    }

    private static Path setupCacheDirectory(CustomCropsPluginImpl plugin) {
        File folder = new File(plugin.getDataFolder(), "libs");
        folder.mkdirs();
        return folder.toPath();
    }

    @Override
    public void close() {
        IOException firstEx = null;

        for (IsolatedClassLoader loader : this.loaders.values()) {
            try {
                loader.close();
            } catch (IOException ex) {
                if (firstEx == null) {
                    firstEx = ex;
                } else {
                    firstEx.addSuppressed(ex);
                }
            }
        }

        if (firstEx != null) {
            firstEx.printStackTrace();
        }
    }

}
