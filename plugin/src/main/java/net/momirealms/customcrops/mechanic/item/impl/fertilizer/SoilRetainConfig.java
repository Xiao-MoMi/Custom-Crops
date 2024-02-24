package net.momirealms.customcrops.mechanic.item.impl.fertilizer;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.SoilRetain;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.impl.AbstractFertilizer;

import java.util.HashMap;
import java.util.HashSet;

public class SoilRetainConfig extends AbstractFertilizer implements SoilRetain {

    private final double chance;

    public SoilRetainConfig(
            String key,
            String itemID,
            int times,
            double chance,
            FertilizerType fertilizerType,
            HashSet<String> potWhitelist,
            boolean beforePlant,
            String icon,
            Requirement[] requirements,
            HashMap<ActionTrigger, Action[]> events) {
        super(key, itemID, times, fertilizerType, potWhitelist, beforePlant, icon, requirements, events);
        this.chance = chance;
    }

    @Override
    public double getChance() {
        return chance;
    }
}
