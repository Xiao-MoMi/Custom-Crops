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

import org.jetbrains.annotations.Nullable;

/**
 * The IdMap interface defines a structure for managing and retrieving objects
 * by their unique identifiers (IDs).
 *
 * @param <T> the type of objects managed by this IdMap
 */
public interface IdMap<T> extends Iterable<T> {

    /**
     * The default value used to indicate that no ID is assigned or available.
     */
    int DEFAULT = -1;

    /**
     * Retrieves the unique identifier associated with a given object.
     *
     * @param value the object whose ID is to be retrieved
     * @return the unique identifier of the object, or -1 if not found
     */
    int getId(T value);

    /**
     * Retrieves an object by its unique identifier.
     *
     * @param index the unique identifier of the object
     * @return the object associated with the given ID, or null if not present
     */
    @Nullable
    T byId(int index);

    /**
     * Retrieves an object by its unique identifier or throws an exception if not found.
     *
     * @param index the unique identifier of the object
     * @return the object associated with the given ID
     * @throws IllegalArgumentException if no object with the given ID exists
     */
    default T byIdOrThrow(int index) {
        T object = this.byId(index);
        if (object == null) {
            throw new IllegalArgumentException("No value with id " + index);
        } else {
            return object;
        }
    }

    /**
     * Retrieves the unique identifier associated with a given object or throws an exception if not found.
     *
     * @param value the object whose ID is to be retrieved
     * @return the unique identifier of the object
     * @throws IllegalArgumentException if no ID for the given object exists
     */
    default int getIdOrThrow(T value) {
        int i = this.getId(value);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + this);
        } else {
            return i;
        }
    }

    /**
     * Returns the total number of entries in this IdMap.
     *
     * @return the number of entries
     */
    int size();
}