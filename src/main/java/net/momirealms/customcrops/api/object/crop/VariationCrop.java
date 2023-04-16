package net.momirealms.customcrops.api.object.crop;

import net.momirealms.customcrops.api.object.ItemMode;

public class VariationCrop {

    private final String id;
    private final ItemMode itemMode;
    private final double chance;

    public VariationCrop(String id, ItemMode itemMode, double chance) {
        this.id = id;
        this.itemMode = itemMode;
        this.chance = chance;
    }

    public String getId() {
        return id;
    }

    public ItemMode getCropMode() {
        return itemMode;
    }

    public double getChance() {
        return chance;
    }
}
