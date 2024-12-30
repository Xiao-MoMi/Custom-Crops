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

package net.momirealms.customcrops.api.core.world.adaptor;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.StringTag;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.common.dependency.Dependency;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class AbstractWorldAdaptor<W> implements WorldAdaptor<W> {

    public static final int CHUNK_VERSION = 2;
    public static final int REGION_VERSION = 1;

    private final Method decompressMethod;
    private final Method compressMethod;

    public AbstractWorldAdaptor() {
        ClassLoader classLoader = BukkitCustomCropsPlugin.getInstance().getDependencyManager().obtainClassLoaderWith(EnumSet.of(Dependency.ZSTD));
        try {
            Class<?> zstd = classLoader.loadClass("com.github.luben.zstd.Zstd");
            decompressMethod = zstd.getMethod("decompress", byte[].class, byte[].class);
            compressMethod = zstd.getMethod("compress", byte[].class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void zstdDecompress(byte[] decompressedData, byte[] compressedData) {
        try {
            decompressMethod.invoke(null, decompressedData, compressedData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        //Zstd.decompress(decompressedData, compressedData);
    }

    protected byte[] zstdCompress(byte[] data) {
        try {
            Object result = compressMethod.invoke(null, (Object) data);
            return (byte[]) result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        //return Zstd.compress(data);
    }

    @Override
    public int compareTo(@NotNull WorldAdaptor<W> o) {
        return Integer.compare(o.priority(), this.priority());
    }

    protected SerializableChunk toSerializableChunk(CustomCropsChunk chunk) {
        ChunkPos chunkPos = chunk.chunkPos();
        return new SerializableChunk(
                chunkPos.x(),
                chunkPos.z(),
                chunk.loadedSeconds(),
                chunk.lastLoadedTime(),
                chunk.sectionsToSave().map(this::toSerializableSection).toList(),
                queueToIntArray(chunk.tickTaskQueue()),
                tickedBlocksToArray(chunk.tickedBlocks())
        );
    }

    protected SerializableSection toSerializableSection(CustomCropsSection section) {
        return new SerializableSection(section.getSectionID(), toCompoundTags(section.blockMap()));
    }

    private List<CompoundTag> toCompoundTags(Map<BlockPos, CustomCropsBlockState> blocks) {
        ArrayList<CompoundTag> tags = new ArrayList<>(blocks.size());
        Map<CustomCropsBlockState, List<Integer>> blockToPosMap = new HashMap<>();
        for (Map.Entry<BlockPos, CustomCropsBlockState> entry : blocks.entrySet()) {
            BlockPos coordinate = entry.getKey();
            CustomCropsBlockState block = entry.getValue();
            List<Integer> coordinates = blockToPosMap.computeIfAbsent(block, k -> new ArrayList<>());
            coordinates.add(coordinate.position());
        }
        for (Map.Entry<CustomCropsBlockState, List<Integer>> entry : blockToPosMap.entrySet()) {
            tags.add(new CompoundTag("", toCompoundMap(entry.getKey(), entry.getValue())));
        }
        return tags;
    }

    private CompoundMap toCompoundMap(CustomCropsBlockState block, List<Integer> pos) {
        CompoundMap map = new CompoundMap();
        int[] result = new int[pos.size()];
        for (int i = 0; i < pos.size(); i++) {
            result[i] = pos.get(i);
        }
        map.put(new StringTag("type", block.type().type().asString()));
        map.put(new IntArrayTag("pos", result));
        map.put(new CompoundTag("data", block.compoundMap().originalMap()));
        return map;
    }

    private int[] tickedBlocksToArray(Set<BlockPos> set) {
        int[] ticked = new int[set.size()];
        int i = 0;
        for (BlockPos pos : set) {
            ticked[i] = pos.position();
            i++;
        }
        return ticked;
    }

    private int[] queueToIntArray(PriorityBlockingQueue<DelayedTickTask> queue) {
        int size = queue.size() * 2;
        int[] tasks = new int[size];
        int i = 0;
        for (DelayedTickTask task : queue) {
            tasks[i * 2] = task.getTime();
            tasks[i * 2 + 1] = task.blockPos().position();
            i++;
        }
        return tasks;
    }
}
