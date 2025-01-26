/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Utility class for handling classes.
 */
public class ClassUtils {

    private ClassUtils() {}

    @Nullable
    public static <T, C> Class<? extends T> findClass(
            @NotNull File file,
            @NotNull Class<T> clazz,
            @NotNull Class<C> type
    ) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }

        URL jarUrl = file.toURI().toURL();
        List<Class<? extends T>> classes = new ArrayList<>();

        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, clazz.getClassLoader());
             JarInputStream jarStream = new JarInputStream(jarUrl.openStream())) {

            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }

                String className = name.substring(0, name.lastIndexOf('.')).replace('/', '.');

                try {
                    Class<?> loadedClass = loader.loadClass(className);
                    if (clazz.isAssignableFrom(loadedClass)) {
                        Type superclassType = loadedClass.getGenericSuperclass();
                        if (superclassType instanceof ParameterizedType parameterizedType) {
                            Type[] typeArguments = parameterizedType.getActualTypeArguments();
                            if (typeArguments.length > 0 && typeArguments[0].equals(type)) {
                                classes.add(loadedClass.asSubclass(clazz));
                            }
                        }
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                }
            }
        }
        
        if (classes.isEmpty()) {
            return null;
        }

        return classes.get(0);
    }
}
