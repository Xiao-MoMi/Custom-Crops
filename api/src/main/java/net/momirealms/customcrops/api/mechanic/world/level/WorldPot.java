package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

public interface WorldPot extends CustomCropsBlock {

    int getWater();

    void setWater(int water);

    String getFertilizer();

    void setFertilizer(String fertilizer);

    int getFertilizerTimes();

    void setFertilizerTimes(int fertilizerTimes);

    Pot getConfig();
}
