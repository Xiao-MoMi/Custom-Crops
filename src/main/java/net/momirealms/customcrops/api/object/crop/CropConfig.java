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

package net.momirealms.customcrops.api.object.crop;

import net.momirealms.customcrops.api.object.BoneMeal;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.action.Action;
import net.momirealms.customcrops.api.object.condition.Condition;
import net.momirealms.customcrops.api.object.condition.DeathCondition;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CropConfig {

    private final String key;
    private final ItemMode itemMode;
    private final String[] bottom_blocks;
    private final int max_points;
    private final HashMap<Integer, StageConfig> stageMap;
    private final Requirement[] plantRequirements;
    private final Requirement[] breakRequirements;
    private final DeathCondition[] deathConditions;
    private final Condition[] growConditions;
    private final BoneMeal[] boneMeals;
    private final Action[] plantActions;
    private final boolean rotation;

    public CropConfig(
          String key,
          ItemMode itemMode,
          int max_points,
          String[] bottom_blocks,
          Requirement[] plantRequirements,
          Requirement[] breakRequirements,
          DeathCondition[] deathConditions,
          Condition[] growConditions,
          HashMap<Integer, StageConfig> stageMap,
          BoneMeal[] boneMeals,
          Action[] plantActions,
          boolean rotation
    ) {
        this.key = key;
        this.itemMode = itemMode;
        this.deathConditions = deathConditions;
        this.plantRequirements = plantRequirements;
        this.breakRequirements = breakRequirements;
        this.max_points = max_points;
        this.bottom_blocks = bottom_blocks;
        this.stageMap = stageMap;
        this.growConditions = growConditions;
        this.boneMeals = boneMeals;
        this.plantActions = plantActions;
        this.rotation = rotation;
    }

    public String getKey() {
        return key;
    }

    @NotNull
    public ItemMode getCropMode() {
        return itemMode;
    }

    @Nullable
    public StageConfig getStageConfig(int stage) {
        return stageMap.get(stage);
    }

    @NotNull
    public String[] getPotWhitelist() {
        return bottom_blocks;
    }

    public int getMaxPoints() {
        return max_points;
    }

    @Nullable
    public Requirement[] getPlantRequirements() {
        return plantRequirements;
    }

    @Nullable
    public Requirement[] getBreakRequirements() {
        return breakRequirements;
    }

    @Nullable
    public DeathCondition[] getDeathConditions() {
        return deathConditions;
    }

    @Nullable
    public Condition[] getGrowConditions() {
        return growConditions;
    }

    @Nullable
    public BoneMeal[] getBoneMeals() {
        return boneMeals;
    }

    @Nullable
    public Action[] getPlantActions() {
        return plantActions;
    }

    public boolean isRotationEnabled() {
        return rotation;
    }
}
