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

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

import java.util.List;
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

    public CompoundMap originalMap() {
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

    public Tag<?> remove(String key) {
        writeLock.lock();
        try {
            return compoundMap.remove(key);
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
        return compoundMapToString("BlockData", compoundMap);
    }

    private String compoundMapToString(String key, CompoundMap compoundMap) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Tag<?>> entry : compoundMap.entrySet()) {
            Tag<?> tag = entry.getValue();
            String tagValue;
            switch (tag.getType()) {
                case TAG_STRING, TAG_BYTE, TAG_DOUBLE, TAG_FLOAT, TAG_INT, TAG_INT_ARRAY, TAG_LONG, TAG_SHORT, TAG_SHORT_ARRAY, TAG_LONG_ARRAY, TAG_BYTE_ARRAY ->
                        tagValue = tag.getValue().toString();
                case TAG_LIST -> {
                    List<Tag<?>> list = (List<Tag<?>>) tag.getValue();
                    StringJoiner listJoiner = new StringJoiner(", ");
                    for (Tag<?> tag2 : list) {
                        if (tag2.getType() == TagType.TAG_COMPOUND) {
                            listJoiner.add(compoundMapToString(tag2.getName(), (CompoundMap) tag2.getValue()));
                        } else {
                            listJoiner.add(tag2.getValue().toString());
                        }
                    }
                    tagValue = "[" + listJoiner + "]";
                }
                case TAG_COMPOUND -> tagValue = compoundMapToString(tag.getName(), (CompoundMap) tag.getValue());
                default -> {
                    continue;
                }
            }
            joiner.add("\"" + entry.getKey() + "\":\"" + tagValue + "\"");
        }
        return key + "{" + joiner + "}";
    }
}
