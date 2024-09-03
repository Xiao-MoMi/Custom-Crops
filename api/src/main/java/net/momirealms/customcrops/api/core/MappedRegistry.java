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

import java.util.*;

/**
 * A registry implementation that supports mapping keys to values and provides
 * methods for registration, lookup, and iteration.
 *
 * @param <K> the type of the keys used for lookup
 * @param <T> the type of the values stored in the registry
 */
public class MappedRegistry<K, T> implements WriteableRegistry<K, T> {

    protected final Map<K, T> byKey = new HashMap<>(1024);
    protected final Map<T, K> byValue = new IdentityHashMap<>(1024);
    protected final ArrayList<T> byID = new ArrayList<>(1024);
    private final Key key;

    /**
     * Constructs a new MappedRegistry with a given unique key.
     *
     * @param key the unique key for this registry
     */
    public MappedRegistry(Key key) {
        this.key = key;
    }

    /**
     * Registers a new key-value pair in the registry.
     *
     * @param key the key associated with the value
     * @param value the value to be registered
     */
    @Override
    public void register(K key, T value) {
        if (byKey.containsKey(key)) return;
        byKey.put(key, value);
        byValue.put(value, key);
        byID.add(value);
    }

    /**
     * Gets the unique key identifier for this registry.
     *
     * @return the key of the registry
     */
    @Override
    public Key key() {
        return key;
    }

    /**
     * Retrieves the index (ID) of a given value in the registry.
     *
     * @param value the value to look up
     * @return the index of the value, or -1 if not found
     */
    @Override
    public int getId(@Nullable T value) {
        return byID.indexOf(value);
    }

    /**
     * Retrieves a value from the registry by its index (ID).
     *
     * @param index the index of the value
     * @return the value at the specified index, or null if out of bounds
     */
    @Nullable
    @Override
    public T byId(int index) {
        return byID.get(index);
    }

    /**
     * Gets the number of entries in the registry.
     *
     * @return the size of the registry
     */
    @Override
    public int size() {
        return byKey.size();
    }

    /**
     * Retrieves a value from the registry by its key.
     *
     * @param key the key of the value
     * @return the value associated with the key, or null if not found
     */
    @Nullable
    @Override
    public T get(@Nullable K key) {
        return byKey.get(key);
    }

    /**
     * Checks if the registry contains a given key.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    @Override
    public boolean containsKey(@Nullable K key) {
        return byKey.containsKey(key);
    }

    /**
     * Checks if the registry contains a given value.
     *
     * @param value the value to check
     * @return true if the value exists, false otherwise
     */
    @Override
    public boolean containsValue(@Nullable T value) {
        return byValue.containsKey(value);
    }

    /**
     * Provides an iterator over the values in the registry.
     *
     * @return an iterator for the registry values
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.byKey.values().iterator();
    }
}
