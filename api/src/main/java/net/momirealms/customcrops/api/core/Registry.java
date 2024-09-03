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

package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The Registry interface defines a structure for a key-value mapping system
 * that supports efficient retrieval and management of entries.
 *
 * @param <K> the type of keys maintained by this registry
 * @param <T> the type of values that the keys map to
 */
public interface Registry<K, T> extends IdMap<T> {

    /**
     * Retrieves the unique key associated with this registry.
     *
     * @return the unique {@link Key} of this registry
     */
    Key key();

    /**
     * Retrieves the unique identifier associated with a value.
     *
     * @param value the value whose identifier is to be retrieved
     * @return the unique identifier of the value, or -1 if not present
     */
    @Override
    int getId(@NotNull T value);

    /**
     * Retrieves a value mapped to the specified key.
     *
     * @param key the key associated with the value to be retrieved
     * @return the value mapped to the specified key, or null if no mapping exists
     */
    @Nullable
    T get(@NotNull K key);

    /**
     * Checks if the registry contains a mapping for the specified key.
     *
     * @param key the key to check for existence
     * @return true if the registry contains a mapping for the key, false otherwise
     */
    boolean containsKey(@NotNull K key);

    /**
     * Checks if the registry contains the specified value.
     *
     * @param value the value to check for existence
     * @return true if the registry contains the specified value, false otherwise
     */
    boolean containsValue(@NotNull T value);
}