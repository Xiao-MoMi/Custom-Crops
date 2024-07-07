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

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
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

    @Override
    public String toString() {
        return compoundMapToString(compoundMap);
    }

    private String compoundMapToString(CompoundMap compoundMap) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Tag<?>> entry : compoundMap.entrySet()) {
            Tag<?> tag = entry.getValue();
            String tagValue;
            switch (tag.getType()) {
                case TAG_STRING, TAG_BYTE, TAG_DOUBLE, TAG_FLOAT, TAG_INT, TAG_INT_ARRAY, TAG_LONG, TAG_SHORT, TAG_SHORT_ARRAY, TAG_LONG_ARRAY, TAG_BYTE_ARRAY ->
                        tagValue = tag.getValue().toString();
                case TAG_COMPOUND -> tagValue = compoundMapToString(tag.getAsCompoundTag().get().getValue());
                case TAG_LIST -> tagValue = tag.getAsListTag().get().getValue().toString();
                default -> {
                    continue;
                }
            }
            joiner.add("\"" + entry.getKey() + "\":\"" + tagValue + "\"");
        }
        return "{" + joiner + "}";
    }
}
