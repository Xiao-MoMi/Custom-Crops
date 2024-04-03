/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassUtils {

    private ClassUtils() {}

    /**
     * Attempts to find a class within a JAR file that extends or implements a given class or interface.
     *
     * @param file  The JAR file in which to search for the class.
     * @param clazz The base class or interface to match against.
     * @param <T>   The type of the base class or interface.
     * @return A Class object representing the found class, or null if not found.
     * @throws IOException            If there is an issue reading the JAR file.
     * @throws ClassNotFoundException If the specified class cannot be found.
     */
    @Nullable
    public static <T> Class<? extends T> findClass(
            @NotNull File file,
            @NotNull Class<T> clazz
    ) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }

        URL jar = file.toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
        List<String> matches = new ArrayList<>();
        List<Class<? extends T>> classes = new ArrayList<>();

        try (JarInputStream stream = new JarInputStream(jar.openStream())) {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                final String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }
                matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
            }

            for (String match : matches) {
                try {
                    Class<?> loaded = loader.loadClass(match);
                    if (clazz.isAssignableFrom(loaded)) {
                        classes.add(loaded.asSubclass(clazz));
                    }
                } catch (NoClassDefFoundError ignored) {
                }
            }
        }
        if (classes.isEmpty()) {
            loader.close();
            return null;
        }
        return classes.get(0);
    }
}
