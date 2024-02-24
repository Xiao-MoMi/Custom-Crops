package net.momirealms.customcrops.mechanic.world.block;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;

import java.util.HashMap;

public class MemoryPot extends AbstractPropertyItem implements WorldPot {

    private int water;
    private String fertilizer;
    private int fertilizerTimes;

    public MemoryPot(String key, HashMap<String, Property<?>> properties) {
        super(key, properties);
    }

    @Override
    public int getWater() {
        return water;
    }

    @Override
    public void setWater(int water) {
        this.water = Math.min(water, getConfig().getStorage());
    }

    @Override
    public String getFertilizer() {
        return fertilizer;
    }

    @Override
    public void setFertilizer(String fertilizer) {
        this.fertilizer = fertilizer;
    }

    @Override
    public int getFertilizerTimes() {
        return fertilizerTimes;
    }

    @Override
    public void setFertilizerTimes(int fertilizerTimes) {
        this.fertilizerTimes = fertilizerTimes;
    }

    @Override
    public Pot getConfig() {
        return CustomCropsPlugin.get().getItemManager().getPotByID(getKey());
    }
}
