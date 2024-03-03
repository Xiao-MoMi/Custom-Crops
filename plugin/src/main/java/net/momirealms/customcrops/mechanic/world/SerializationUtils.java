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

package net.momirealms.customcrops.mechanic.world;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.StringTag;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializationUtils {

    public static SerializableChunk toSerializableChunk(CChunk chunk) {
        ChunkCoordinate chunkCoordinate = chunk.getChunkCoordinate();
        return new SerializableChunk(
                chunkCoordinate.x(),
                chunkCoordinate.z(),
                chunk.getLoadedSeconds(),
                chunk.getLastLoadedTime(),
                toCompoundTags(chunk.getLoadedBlocks()),
                new ArrayList<>()
        );
    }

    public static List<CompoundTag> toCompoundTags(Map<ChunkPos, CustomCropsBlock> map) {
        ArrayList<CompoundTag> tags = new ArrayList<>(map.size());
        Map<CustomCropsBlock, List<Integer>> blockToPosMap = new HashMap<>();
        for (Map.Entry<ChunkPos, CustomCropsBlock> entry : map.entrySet()) {
            ChunkPos coordinate = entry.getKey();
            CustomCropsBlock block = entry.getValue();
            List<Integer> coordinates = blockToPosMap.computeIfAbsent(block, k -> new ArrayList<>());
            coordinates.add(coordinate.getPosition());
        }
        for (Map.Entry<CustomCropsBlock, List<Integer>> entry : blockToPosMap.entrySet()) {
            tags.add(new CompoundTag("", toCompoundMap(entry.getKey(), entry.getValue())));
        }
        return tags;
    }

    public static CompoundMap toCompoundMap(CustomCropsBlock block, List<Integer> pos) {
        CompoundMap map = new CompoundMap();
        int[] result = new int[pos.size()];
        for (int i = 0; i < pos.size(); i++) {
            result[i] = pos.get(i);
        }
        map.put(new StringTag("type", block.getType().name()));
        map.put(new IntArrayTag("pos", result));
        map.put(new CompoundTag("data", block.getCompoundMap()));
        return map;
    }
}
