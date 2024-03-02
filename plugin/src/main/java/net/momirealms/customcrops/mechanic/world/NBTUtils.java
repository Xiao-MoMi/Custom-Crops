package net.momirealms.customcrops.mechanic.world;

import com.flowpowered.nbt.*;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBTUtils {

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
        System.out.println(blockToPosMap.size());
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
