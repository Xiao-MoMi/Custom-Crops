package net.momirealms.customcrops.mechanic.item.impl.fertilizer;

import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.YieldIncrease;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.impl.AbstractFertilizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class YieldIncreaseConfig extends AbstractFertilizer implements YieldIncrease {

    private final List<Pair<Double, Integer>> pairs;

    public YieldIncreaseConfig(
            String key,
            String itemID,
            int times,
            FertilizerType fertilizerType,
            HashSet<String> potWhitelist,
            boolean beforePlant,
            String icon,
            Requirement[] requirements,
            List<Pair<Double, Integer>> pairs,
            HashMap<ActionTrigger, Action[]> events) {
        super(key, itemID, times, fertilizerType, potWhitelist, beforePlant, icon, requirements, events);
        this.pairs = pairs;
    }

    @Override
    public int getAmountBonus() {
        for (Pair<Double, Integer> pair : pairs) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }
}
