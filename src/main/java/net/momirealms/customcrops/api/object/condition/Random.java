package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.api.object.world.SimpleLocation;

public class Random implements Condition {

    private final double chance;

    public Random(double chance) {
        this.chance = chance;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        return Math.random() < chance;
    }
}
