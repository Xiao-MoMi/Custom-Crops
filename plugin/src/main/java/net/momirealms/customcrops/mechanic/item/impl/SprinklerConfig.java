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
import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class SprinklerConfig extends AbstractEventItem implements Sprinkler {

    private final String key;
    private final int range;
    private final int storage;
    private final int water;
    private final boolean infinite;
    private final String twoDItem;
    private final String threeDItem;
    private final String threeDItemWithWater;
    private final WaterBar waterBar;
    private final HashSet<String> potWhitelist;
    private final ItemCarrier itemCarrier;
    private final PassiveFillMethod[] passiveFillMethods;
    private final Requirement[] placeRequirements;
    private final Requirement[] breakRequirements;
    private final Requirement[] useRequirements;

    public SprinklerConfig(
            String key,
            ItemCarrier itemCarrier,
            String twoDItem,
            String threeDItem,
            String threeDItemWithWater,
            int range,
            int storage,
            int water,
            boolean infinite,
            WaterBar waterBar,
            HashSet<String> potWhitelist,
            PassiveFillMethod[] passiveFillMethods,
            HashMap<ActionTrigger, Action[]> actionMap,
            Requirement[] placeRequirements,
            Requirement[] breakRequirements,
            Requirement[] useRequirements
    ) {
        super(actionMap);
        this.key = key;
        this.itemCarrier = itemCarrier;
        this.twoDItem = twoDItem;
        this.threeDItem = threeDItem;
        this.threeDItemWithWater = threeDItemWithWater;
        this.range = range;
        this.storage = storage;
        this.infinite = infinite;
        this.water = water;
        this.waterBar = waterBar;
        this.potWhitelist = potWhitelist;
        this.passiveFillMethods = passiveFillMethods;
        this.placeRequirements = placeRequirements;
        this.breakRequirements = breakRequirements;
        this.useRequirements = useRequirements;
    }

    @Nullable
    @Override
    public String get2DItemID() {
        return twoDItem;
    }

    @NotNull
    @Override
    public String get3DItemID() {
        return threeDItem;
    }

    @Override
    @Nullable
    public String get3DItemWithWater() {
        return threeDItemWithWater;
    }

    @Override
    public int getStorage() {
        return storage;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean isInfinite() {
        return infinite;
    }

    @Override
    public int getWater() {
        return water;
    }

    @Override
    public HashSet<String> getPotWhitelist() {
        return potWhitelist;
    }

    @Override
    public ItemCarrier getItemCarrier() {
        return itemCarrier;
    }

    @Override
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
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
}
