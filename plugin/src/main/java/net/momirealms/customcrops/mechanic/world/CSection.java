package net.momirealms.customcrops.mechanic.world;

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CSection implements CustomCropsSection {

    private final int sectionID;
    private final ConcurrentHashMap<ChunkPos, CustomCropsBlock> blocks;

    public CSection(int sectionID) {
        this.sectionID = sectionID;
        this.blocks = new ConcurrentHashMap<>();
    }

    public CSection(int sectionID, ConcurrentHashMap<ChunkPos, CustomCropsBlock> blocks) {
        this.blocks = blocks;
        this.sectionID = sectionID;
    }

    @Override
    public int getSectionID() {
        return sectionID;
    }

    @Override
    public CustomCropsBlock getBlockAt(ChunkPos pos) {
        return blocks.get(pos);
    }

    @Override
    public CustomCropsBlock removeBlockAt(ChunkPos pos) {
        return blocks.remove(pos);
    }

    @Override
    public CustomCropsBlock addBlockAt(ChunkPos pos, CustomCropsBlock block) {
        return blocks.put(pos, block);
    }

    @Override
    public boolean canPrune() {
        return blocks.size() == 0;
    }

    @Override
    public CustomCropsBlock[] getBlocks() {
        return blocks.values().toArray(new CustomCropsBlock[0]);
    }

    public Map<ChunkPos, CustomCropsBlock> getBlockMap() {
        return blocks;
    }
}
