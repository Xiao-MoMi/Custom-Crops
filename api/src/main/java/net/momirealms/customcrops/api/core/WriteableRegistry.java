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

/**
 * The WriteableRegistry interface extends the Registry interface, adding the capability
 * to register new key-value pairs. This interface is used to define registries that
 * allow modifications.
 *
 * @param <K> the type of the keys used for lookup
 * @param <T> the type of the values stored in the registry
 */
public interface WriteableRegistry<K, T> extends Registry<K, T> {

    /**
     * Registers a new key-value pair in the registry.
     * This method allows adding new entries to the registry dynamically.
     *
     * @param key the key associated with the value
     * @param value the value to be registered
     */
    void register(K key, T value);
}