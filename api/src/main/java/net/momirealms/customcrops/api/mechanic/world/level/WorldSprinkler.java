package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

public interface WorldSprinkler extends CustomCropsBlock {

    int getWater();

    void setWater(int water);

    String getKey();

    Sprinkler getConfig();
}
