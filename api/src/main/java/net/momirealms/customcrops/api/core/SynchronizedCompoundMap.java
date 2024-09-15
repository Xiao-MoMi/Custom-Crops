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

/**
 * A thread-safe wrapper around a CompoundMap that provides synchronized access
 * to the underlying map for reading and writing operations.
 */
public class SynchronizedCompoundMap {

    private final CompoundMap compoundMap;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final java.util.concurrent.locks.Lock readLock = rwLock.readLock();
    private final java.util.concurrent.locks.Lock writeLock = rwLock.writeLock();

    /**
     * Constructs a new SynchronizedCompoundMap with the specified CompoundMap.
     *
     * @param compoundMap the underlying CompoundMap to wrap
     */
    public SynchronizedCompoundMap(CompoundMap compoundMap) {
        this.compoundMap = compoundMap;
    }

    /**
     * Returns the original underlying CompoundMap.
     *
     * @return the original CompoundMap
     */
    public CompoundMap originalMap() {
        return compoundMap;
    }

    /**
     * Retrieves a Tag from the map using the specified key.
     *
     * @param key the key to look up
     * @return the Tag associated with the key, or null if not found
     */
    public Tag<?> get(String key) {
        readLock.lock();
        try {
            return compoundMap.get(key);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Puts a Tag into the map with the specified key, returning the previous
     * value associated with the key, if any.
     *
     * @param key the key to associate with the Tag
     * @param tag the Tag to insert
     * @return the previous Tag associated with the key, or null if none
     */
    public Tag<?> put(String key, Tag<?> tag) {
        writeLock.lock();
        try {
            return compoundMap.put(key, tag);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes a Tag from the map with the specified key.
     *
     * @param key the key whose mapping is to be removed
     * @return the Tag previously associated with the key, or null if none
     */
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
        return compoundMapToString("NBTData", compoundMap);
    }

    public String asString() {
        return compoundMapToString("", compoundMap);
    }

    /**
     * Recursively converts a CompoundMap to a string representation.
     *
     * @param key the key associated with the CompoundMap
     * @param compoundMap the CompoundMap to convert
     * @return a string representation of the CompoundMap
     */
    @SuppressWarnings("unchecked")
    private String compoundMapToString(String key, CompoundMap compoundMap) {
        StringJoiner joiner = new StringJoiner(",");
        for (Map.Entry<String, Tag<?>> entry : compoundMap.entrySet()) {
            Tag<?> tag = entry.getValue();
            String tagValue;
            switch (tag.getType()) {
                case TAG_STRING, TAG_BYTE, TAG_DOUBLE, TAG_FLOAT, TAG_INT, TAG_INT_ARRAY,
                     TAG_LONG, TAG_SHORT, TAG_SHORT_ARRAY, TAG_LONG_ARRAY, TAG_BYTE_ARRAY ->
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
                    continue; // skip unsupported tag types
                }
            }
            joiner.add(entry.getKey() + "=" + tagValue);
        }
        if (key.isEmpty()) {
            return "[" + joiner + "]";
        } else {
            return key + "=[" + joiner + "]";
        }
    }
}
