package net.momirealms.customcrops.mechanic.world;

import com.flowpowered.nbt.*;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;

import java.util.ArrayList;
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

    public static List<CompoundTag> toCompoundTags(Map<SimpleLocation, CustomCropsBlock> map) {
        ArrayList<CompoundTag> tags = new ArrayList<>(map.size());
        for (Map.Entry<SimpleLocation, CustomCropsBlock> blockEntry : map.entrySet()) {
            tags.add(new CompoundTag("", toCompoundMap(blockEntry.getKey(), blockEntry.getValue())));
        }
        return tags;
    }

    public static CompoundMap toCompoundMap(SimpleLocation location, CustomCropsBlock block) {
        CompoundMap map = new CompoundMap();
        map.put(new StringTag("type", block.getType().name()));
        map.put(new IntArrayTag("pos", new int[]{location.getX(), location.getY(), location.getZ()}));
        map.put(new CompoundTag("data", block.getCompoundMap()));
        return map;
    }
}
