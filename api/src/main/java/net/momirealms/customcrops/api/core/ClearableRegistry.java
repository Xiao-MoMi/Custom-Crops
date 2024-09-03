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
 * The ClearableRegistry interface extends WriteableRegistry and provides
 * an additional method to clear all entries from the registry.
 *
 * @param <K> the type of keys maintained by this registry
 * @param <T> the type of mapped values
 */
public interface ClearableRegistry<K, T> extends WriteableRegistry<K, T> {

    /**
     * Clears all entries from the registry.
     * This operation removes all key-value mappings from the registry,
     * leaving it empty.
     */
    void clear();
}