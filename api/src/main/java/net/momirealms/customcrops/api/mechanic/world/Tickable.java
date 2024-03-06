package net.momirealms.customcrops.api.mechanic.world;

import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;

public interface Tickable {

    void tick(int interval, CustomCropsChunk chunk);
}
