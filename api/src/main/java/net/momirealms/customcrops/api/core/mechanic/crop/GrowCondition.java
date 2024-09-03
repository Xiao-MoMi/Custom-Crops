/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.mechanic.crop;

import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;

/**
 * Represents a condition for a crop to grow to the next stage.
 * The growth condition specifies the requirements that must be met for the crop to progress,
 * as well as the number of points to be added to the crop's growth when the condition is met.
 */
public class GrowCondition {

    private final Requirement<CustomCropsBlockState>[] requirements;
    private final int pointToAdd;

    /**
     * Constructs a new GrowCondition with the specified requirements and growth points.
     *
     * @param requirements The array of {@link Requirement} instances that must be met for the crop to grow.
     * @param pointToAdd The number of points to be added to the crop's growth when the condition is satisfied.
     */
    public GrowCondition(Requirement<CustomCropsBlockState>[] requirements, int pointToAdd) {
        this.requirements = requirements;
        this.pointToAdd = pointToAdd;
    }

    /**
     * Retrieves the number of growth points to be added when this condition is met.
     *
     * @return The number of points to add to the crop's growth.
     */
    public int pointToAdd() {
        return pointToAdd;
    }

    /**
     * Checks if the growth condition is met in the given context.
     * This method evaluates all the requirements associated with this condition to determine
     * whether the crop can grow.
     *
     * @param context The {@link Context} in which the requirements are evaluated.
     * @return True if all requirements are satisfied; false otherwise.
     */
    public boolean isMet(Context<CustomCropsBlockState> context) {
        return RequirementManager.isSatisfied(context, requirements);
    }
}