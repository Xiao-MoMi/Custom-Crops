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

package net.momirealms.customcrops.mechanic.item.impl;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;

import java.util.HashMap;
import java.util.HashSet;

public class AbstractFertilizer extends AbstractEventItem implements Fertilizer {

    private final String key;
    private final String itemID;
    private final int times;
    private final FertilizerType fertilizerType;
    private final HashSet<String> potWhitelist;
    private final boolean beforePlant;
    private final String icon;
    private final Requirement[] requirements;

    public AbstractFertilizer(
            String key,
            String itemID,
            int times,
            FertilizerType fertilizerType,
            HashSet<String> potWhitelist,
            boolean beforePlant,
            String icon,
            Requirement[] requirements,
            HashMap<ActionTrigger, Action[]> actionMap
    ) {
        super(actionMap);
        this.key = key;
        this.itemID = itemID;
        this.times = times;
        this.fertilizerType = fertilizerType;
        this.potWhitelist = potWhitelist;
        this.beforePlant = beforePlant;
        this.icon = icon;
        this.requirements = requirements;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getItemID() {
        return itemID;
    }

    @Override
    public int getTimes() {
        return times;
    }

    @Override
    public FertilizerType getFertilizerType() {
        return fertilizerType;
    }

    @Override
    public HashSet<String> getPotWhitelist() {
        return potWhitelist;
    }

    @Override
    public boolean isBeforePlant() {
        return beforePlant;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public Requirement[] getRequirements() {
        return requirements;
    }
}
