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

import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;

import java.util.HashMap;
import java.util.HashSet;

public class PotConfig extends AbstractEventItem implements Pot {

    private String key;
    private final int storage;
    private final String dryModel;
    private final String wetModel;
    private boolean enableFertilizedAppearance;
    private final HashMap<FertilizerType, Pair<String, String>> fertilizedPotMap;
    private final PassiveFillMethod[] passiveFillMethods;
    private WaterBar waterBar;
    private final Requirement[] placeRequirements;
    private final Requirement[] breakRequirements;
    private final Requirement[] useRequirements;

    public PotConfig(
            String key,
            int storage,
            String dryModel,
            String wetModel,
            boolean enableFertilizedAppearance,
            HashMap<FertilizerType, Pair<String, String>> fertilizedPotMap,
            WaterBar waterBar,
            PassiveFillMethod[] passiveFillMethods,
            HashMap<ActionTrigger, Action[]> actionMap,
            Requirement[] placeRequirements,
            Requirement[] breakRequirements,
            Requirement[] useRequirements
    ) {
        super(actionMap);
        this.key = key;
        this.storage = storage;
        this.enableFertilizedAppearance = enableFertilizedAppearance;
        this.fertilizedPotMap = fertilizedPotMap;
        this.passiveFillMethods = passiveFillMethods;
        this.dryModel = dryModel;
        this.wetModel = wetModel;
        this.waterBar = waterBar;
        this.placeRequirements = placeRequirements;
        this.breakRequirements = breakRequirements;
        this.useRequirements = useRequirements;
    }

    @Override
    public int getStorage() {
        return 0;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public HashSet<String> getPotBlocks() {
        HashSet<String> set = new HashSet<>();
        set.add(wetModel);
        set.add(dryModel);
        for (Pair<String, String> pair : fertilizedPotMap.values()) {
            set.add(pair.left());
            set.add(pair.right());
        }
        return set;
    }

    @Override
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }

    @Override
    public String getDryItem() {
        return dryModel;
    }

    @Override
    public String getWetItem() {
        return wetModel;
    }

    @Override
    public Requirement[] getPlaceRequirements() {
        return placeRequirements;
    }

    @Override
    public Requirement[] getBreakRequirements() {
        return breakRequirements;
    }

    @Override
    public Requirement[] getUseRequirements() {
        return useRequirements;
    }
}
