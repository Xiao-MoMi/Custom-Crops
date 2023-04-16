package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.world.SimpleLocation;

public class WaterMoreThan implements Condition {

    private final int amount;

    public WaterMoreThan(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean isMet(SimpleLocation crop_loc) {
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(crop_loc.add(0,-1,0));
        if (pot == null) return false;
        return pot.getWater() > amount;
    }
}
