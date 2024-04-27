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

package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.condition.Conditions;
import net.momirealms.customcrops.api.mechanic.condition.DeathConditions;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public interface Crop extends KeyItem {

    /**
     * Get the id of the seed
     *
     * @return seed ID
     */
    String getSeedItemID();

    /**
     * Get the max points to grow
     *
     * @return max points
     */
    int getMaxPoints();

    /**
     * Get the requirements for planting
     *
     * @return requirements for planting
     */
    Requirement[] getPlantRequirements();

    /**
     * Get the requirements for breaking
     *
     * @return requirements for breaking
     */
    Requirement[] getBreakRequirements();

    /**
     * Get the requirements for interactions
     *
     * @return requirements for interactions
     */
    Requirement[] getInteractRequirements();

    /**
     * Get the conditions to grow
     *
     * @return conditions to grow
     */
    Conditions getGrowConditions();

    /**
     * Get the conditions of death
     *
     * @return conditions of death
     */
    DeathConditions[] getDeathConditions();

    /**
     * Get the available bone meals
     *
     * @return bone meals
     */
    BoneMeal[] getBoneMeals();

    /**
     * If the crop has rotations
     */
    boolean hasRotation();

    /**
     * Trigger actions
     *
     * @param trigger action trigger
     * @param state player state
     */
    void trigger(ActionTrigger trigger, State state);

    /**
     * Get the stage config by point
     *
     * @param point point
     * @return stage config
     */
    @Nullable
    Stage getStageByPoint(int point);

    /**
     * Get the stage item ID by point
     * This is always NotNull if the point is no lower than 0
     *
     * @param point point
     * @return the stage item ID
     */
    @NotNull
    String getStageItemByPoint(int point);

    /**
     * Get stage config by stage item ID
     *
     * @param id item id
     * @return stage config
     */
    @Nullable
    Stage getStageByItemID(String id);

    /**
     * Get all the stages
     *
     * @return stages
     */
    Collection<? extends Stage> getStages();

    /**
     * Get the pots to plant
     *
     * @return whitelisted pots
     */
    HashSet<String> getPotWhitelist();

    /**
     * Get the carrier of this crop
     *
     * @return carrier of this crop
     */
    ItemCarrier getItemCarrier();

    interface Stage {

        /**
         * Get the crop config
         *
         * @return crop config
         */
        Crop getCrop();

        /**
         * Get the offset of the hologram
         *
         * @return offset
         */
        double getHologramOffset();

        /**
         * Get the stage item ID
         * This can be null if this point doesn't have any state change
         *
         * @return stage item ID
         */
        @Nullable
        String getStageID();

        /**
         * Get the point of this stage
         *
         * @return point
         */
        int getPoint();

        /**
         * Trigger actions
         *
         * @param trigger action trigger
         * @param state player state
         */
        void trigger(ActionTrigger trigger, State state);

        /**
         * Get the requirements for interactions
         *
         * @return requirements for interactions
         */
        Requirement[] getInteractRequirements();

        /**
         * Get the requirements for breaking
         *
         * @return requirements for breaking
         */
        Requirement[] getBreakRequirements();
    }
}
