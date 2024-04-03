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
import net.momirealms.customcrops.util.ConfigUtils;

import java.util.HashMap;
import java.util.HashSet;

public class PotConfig extends AbstractEventItem implements Pot {

    private final String key;
    private final int storage;
    private final String dryModel;
    private final String wetModel;
    private final boolean enableFertilizedAppearance;
    private final HashMap<FertilizerType, Pair<String, String>> fertilizedPotMap;
    private final PassiveFillMethod[] passiveFillMethods;
    private final WaterBar waterBar;
    private final Requirement[] placeRequirements;
    private final Requirement[] breakRequirements;
    private final Requirement[] useRequirements;
    private final boolean acceptRainDrop;
    private final boolean acceptNearbyWater;
    private final boolean isVanillaBlock;

    public PotConfig(
            String key,
            int storage,
            boolean acceptRainDrop,
            boolean acceptNearbyWater,
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
        this.acceptRainDrop = acceptRainDrop;
        this.acceptNearbyWater = acceptNearbyWater;
        this.enableFertilizedAppearance = enableFertilizedAppearance;
        this.fertilizedPotMap = fertilizedPotMap;
        this.passiveFillMethods = passiveFillMethods;
        this.dryModel = dryModel;
        this.wetModel = wetModel;
        this.waterBar = waterBar;
        this.placeRequirements = placeRequirements;
        this.breakRequirements = breakRequirements;
        this.useRequirements = useRequirements;
        this.isVanillaBlock = ConfigUtils.isVanillaItem(dryModel) && ConfigUtils.isVanillaItem(wetModel);
    }

    @Override
    public int getStorage() {
        return storage;
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

    @Override
    public WaterBar getWaterBar() {
        return waterBar;
    }

    @Override
    public boolean isRainDropAccepted() {
        return acceptRainDrop;
    }

    @Override
    public boolean isNearbyWaterAccepted() {
        return acceptNearbyWater;
    }

    @Override
    public String getBlockState(boolean water, FertilizerType type) {
        if (type != null && enableFertilizedAppearance) {
            return water ? fertilizedPotMap.get(type).right() : fertilizedPotMap.get(type).left();
        } else {
            return water ? wetModel : dryModel;
        }
    }

    @Override
    public boolean isVanillaBlock() {
        return isVanillaBlock;
    }

    @Override
    public boolean isWetPot(String id) {
        if (id.equals(getWetItem())) {
            return true;
        }
        for (Pair<String, String> pair : fertilizedPotMap.values()) {
            if (pair.right().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
