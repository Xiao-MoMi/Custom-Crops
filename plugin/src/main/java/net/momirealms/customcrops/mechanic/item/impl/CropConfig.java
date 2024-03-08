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
import net.momirealms.customcrops.api.mechanic.condition.Conditions;
import net.momirealms.customcrops.api.mechanic.condition.DeathConditions;
import net.momirealms.customcrops.api.mechanic.item.BoneMeal;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class CropConfig extends AbstractEventItem implements Crop {

    private final String key;
    private final String seedID;
    private final int maxPoints;
    private final boolean rotation;
    private final ItemCarrier carrier;
    private final Requirement[] plantRequirements;
    private final Requirement[] breakRequirements;
    private final Requirement[] interactRequirements;
    private final BoneMeal[] boneMeals;
    private final Conditions growConditions;
    private final HashSet<String> whitelistPots;
    private final DeathConditions[] deathConditions;
    private final HashMap<Integer, CropStageConfig> point2StageConfigMap;
    private final HashMap<String, CropStageConfig> item2StageConfigMap;

    public CropConfig(
            String key,
            String seedID,
            ItemCarrier carrier,
            HashSet<String> whitelistPots,
            boolean rotation,
            int maxPoints,
            BoneMeal[] boneMeals,
            Conditions growConditions,
            DeathConditions[] deathConditions,
            HashMap<ActionTrigger, Action[]> actionMap,
            HashMap<Integer, CropStageConfig> point2StageConfigMap,
            Requirement[] plantRequirements,
            Requirement[] breakRequirements,
            Requirement[] interactRequirements
    ) {
        super(actionMap);
        this.key = key;
        this.seedID = seedID;
        this.boneMeals = boneMeals;
        this.rotation = rotation;
        this.maxPoints = maxPoints;
        this.growConditions = growConditions;
        this.deathConditions = deathConditions;
        this.plantRequirements = plantRequirements;
        this.breakRequirements = breakRequirements;
        this.interactRequirements = interactRequirements;
        this.point2StageConfigMap = point2StageConfigMap;
        this.whitelistPots = whitelistPots;
        this.carrier = carrier;
        this.item2StageConfigMap = new HashMap<>();
        for (CropStageConfig cropStageConfig : point2StageConfigMap.values()) {
            if (cropStageConfig.getStageID() != null) {
                this.item2StageConfigMap.put(cropStageConfig.getStageID(), cropStageConfig);
            }
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getSeedItemID() {
        return seedID;
    }

    @Override
    public int getMaxPoints() {
        return maxPoints;
    }

    @Override
    public Requirement[] getPlantRequirements() {
        return plantRequirements;
    }

    @Override
    public Requirement[] getBreakRequirements() {
        return breakRequirements;
    }

    @Override
    public Requirement[] getInteractRequirements() {
        return interactRequirements;
    }

    @Override
    public Conditions getGrowConditions() {
        return growConditions;
    }

    @Override
    public DeathConditions[] getDeathConditions() {
        return deathConditions;
    }

    @Override
    public BoneMeal[] getBoneMeals() {
        return boneMeals;
    }

    @Override
    public boolean isRotation() {
        return rotation;
    }

    @Override
    public Stage getStageByPoint(int point) {
        return point2StageConfigMap.get(point);
    }

    @Override
    public String getStageItemByPoint(int point) {
        if (point >= 0) {
            Stage stage = point2StageConfigMap.get(point);
            if (stage != null) {
                String id = stage.getStageID();
                if (id == null) return getStageItemByPoint(point-1);
                return id;
            } else {
                return getStageItemByPoint(point-1);
            }
        }
        return null;
    }

    @Override
    public Stage getStageByItemID(String itemID) {
        return item2StageConfigMap.get(itemID);
    }

    @Override
    public Collection<? extends Stage> getStages() {
        return new ArrayList<>(point2StageConfigMap.values());
    }

    @Override
    public HashSet<String> getPotWhitelist() {
        return whitelistPots;
    }

    @Override
    public ItemCarrier getItemCarrier() {
        return carrier;
    }

    public static class CropStageConfig extends AbstractEventItem implements Crop.Stage {

        @Nullable
        private final String stageID;

        private final int point;
        private final double hologramOffset;
        private final Requirement[] interactRequirements;
        private final Requirement[] breakRequirements;

        public CropStageConfig(
                @Nullable String stageID,
                int point,
                double hologramOffset,
                HashMap<ActionTrigger, Action[]> actionMap,
                Requirement[] interactRequirements,
                Requirement[] breakRequirements
        ) {
            super(actionMap);
            this.stageID = stageID;
            this.point = point;
            this.hologramOffset = hologramOffset;
            this.interactRequirements = interactRequirements;
            this.breakRequirements = breakRequirements;
        }

        @Override
        public double getHologramOffset() {
            return hologramOffset;
        }

        @Nullable
        @Override
        public String getStageID() {
            return stageID;
        }

        @Override
        public int getPoint() {
            return point;
        }

        @Override
        public Requirement[] getInteractRequirements() {
            return interactRequirements;
        }

        @Override
        public Requirement[] getBreakRequirements() {
            return breakRequirements;
        }
    }
}
