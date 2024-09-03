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

/**
 * ClearableMappedRegistry is a concrete implementation of the ClearableRegistry interface.
 * It extends MappedRegistry and provides the capability to clear all entries.
 *
 * @param <K> the type of keys maintained by this registry
 * @param <T> the type of mapped values
 */
public class ClearableMappedRegistry<K, T> extends MappedRegistry<K, T> implements ClearableRegistry<K, T> {

    /**
     * Constructs a new ClearableMappedRegistry with a unique key.
     *
     * @param key the unique key for this registry
     */
    public ClearableMappedRegistry(Key key) {
        super(key);
    }

    /**
     * Clears all entries from the registry.
     * This operation removes all key-value mappings from the registry,
     * leaving it empty.
     */
    @Override
    public void clear() {
        super.byID.clear();  // Clears the list of values indexed by ID
        super.byKey.clear(); // Clears the map of keys to values
        super.byValue.clear(); // Clears the map of values to keys
    }
}