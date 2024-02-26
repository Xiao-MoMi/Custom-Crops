/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.mechanic.item.impl.fertilizer;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.Variation;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.impl.AbstractFertilizer;

import java.util.HashMap;
import java.util.HashSet;

public class VariationConfig extends AbstractFertilizer implements Variation {

    private final double chanceBonus;

    public VariationConfig(
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
        this.chanceBonus = chance;
    }

    @Override
    public double getChanceBonus() {
        return chanceBonus;
    }
}
