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

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
    int DEFAULT = -1;

    int getId(T value);

    @Nullable
    T byId(int index);

    default T byIdOrThrow(int index) {
        T object = this.byId(index);
        if (object == null) {
            throw new IllegalArgumentException("No value with id " + index);
        } else {
            return object;
        }
    }

    default int getIdOrThrow(T value) {
        int i = this.getId(value);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + this);
        } else {
            return i;
        }
    }

    int size();
}
