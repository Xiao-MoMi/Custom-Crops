package net.momirealms.customcrops.mechanic.item.impl.fertilizer;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.QualityCrop;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.impl.AbstractFertilizer;

import java.util.HashMap;
import java.util.HashSet;

public class QualityCropConfig extends AbstractFertilizer implements QualityCrop {

    private final double[] ratio;
    private final double chance;

    public QualityCropConfig(
            String key,
            String itemID,
            int times,
            double chance,
            FertilizerType fertilizerType,
            HashSet<String> potWhitelist,
            boolean beforePlant,
            String icon,
            Requirement[] requirements,
            double[] ratio,
            HashMap<ActionTrigger, Action[]> events
    ) {
        super(key, itemID, times, fertilizerType, potWhitelist, beforePlant, icon, requirements, events);
        this.ratio = ratio;
        this.chance = chance;
    }

    @Override
    public double getChance() {
        return chance;
    }

    @Override
    public double[] getRatio() {
        return ratio;
    }
}
