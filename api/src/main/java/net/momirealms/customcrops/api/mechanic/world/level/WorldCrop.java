package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

public interface WorldCrop extends CustomCropsBlock {

    int getPoint();

    void setPoint(int point);

    Crop getConfig();
}
