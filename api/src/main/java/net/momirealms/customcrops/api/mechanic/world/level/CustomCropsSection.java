package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

public interface CustomCropsSection {

    int getSectionID();

    CustomCropsBlock getBlockAt(ChunkPos pos);

    CustomCropsBlock removeBlockAt(ChunkPos pos);

    CustomCropsBlock addBlockAt(ChunkPos pos, CustomCropsBlock block);

    boolean canPrune();

    CustomCropsBlock[] getBlocks();
}
