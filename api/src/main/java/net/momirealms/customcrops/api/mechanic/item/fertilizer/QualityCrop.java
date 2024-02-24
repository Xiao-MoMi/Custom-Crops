package net.momirealms.customcrops.api.mechanic.item.fertilizer;

import net.momirealms.customcrops.api.mechanic.item.Fertilizer;

public interface QualityCrop extends Fertilizer {
    double getChance();

    double[] getRatio();
}
