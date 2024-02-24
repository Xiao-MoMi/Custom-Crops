package net.momirealms.customcrops.mechanic.world.block;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;

import java.util.HashMap;

public class MemorySprinkler extends AbstractPropertyItem implements WorldSprinkler {

    private int water;

    public MemorySprinkler(String key, int water, HashMap<String, Property<?>> properties) {
        super(key, properties);
        this.water = water;
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
    public Sprinkler getConfig() {
        return CustomCropsPlugin.get().getItemManager().getSprinklerByID(getKey());
    }
}
