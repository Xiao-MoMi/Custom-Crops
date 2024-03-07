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

package net.momirealms.customcrops.api.mechanic.world;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedCompoundMap {

    private final CompoundMap compoundMap;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final java.util.concurrent.locks.Lock readLock = rwLock.readLock();
    private final java.util.concurrent.locks.Lock writeLock = rwLock.writeLock();

    public SynchronizedCompoundMap(CompoundMap compoundMap) {
        this.compoundMap = compoundMap;
    }

    public CompoundMap getOriginalMap() {
        return compoundMap;
    }

    public Tag<?> get(String key) {
        readLock.lock();
        try {
            return compoundMap.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public Tag<?> put(String key, Tag<?> tag) {
        writeLock.lock();
        try {
            return compoundMap.put(key, tag);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronizedCompoundMap that = (SynchronizedCompoundMap) o;
        return Objects.equals(compoundMap, that.compoundMap);
    }
}
