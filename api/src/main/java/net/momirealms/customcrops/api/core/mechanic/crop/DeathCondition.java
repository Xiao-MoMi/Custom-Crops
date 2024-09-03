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
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import org.jetbrains.annotations.Nullable;


/**
 * Represents a condition that determines whether a crop will die.
 */
public class DeathCondition {

    private final Requirement<CustomCropsBlockState>[] requirements;
    private final String deathStage;
    private final ExistenceForm existenceForm;
    private final int deathDelay;

    /**
     * Constructs a new DeathCondition with the specified requirements, death stage, existence form, and death delay.
     *
     * @param requirements The array of {@link Requirement} instances that must be met for the crop to die.
     * @param deathStage The stage ID to transition to when the crop dies. Can be null if there is no specific death stage.
     * @param existenceForm The {@link ExistenceForm} representing the state of the crop after death.
     * @param deathDelay The delay in ticks before the crop transitions to the death stage after the condition is met.
     */
    public DeathCondition(Requirement<CustomCropsBlockState>[] requirements, String deathStage, ExistenceForm existenceForm, int deathDelay) {
        this.requirements = requirements;
        this.deathStage = deathStage;
        this.existenceForm = existenceForm;
        this.deathDelay = deathDelay;
    }

    /**
     * Retrieves the stage ID to transition to upon death.
     *
     * @return The stage ID for the death state, or null if there is no specific death stage.
     */
    @Nullable
    public String deathStage() {
        return deathStage;
    }

    /**
     * Retrieves the delay in ticks before transitioning to the death stage.
     *
     * @return The delay before the crop dies, in ticks.
     */
    public int deathDelay() {
        return deathDelay;
    }

    /**
     * Checks if the death condition is met in the given context.
     * This method evaluates all the requirements associated with this condition to determine
     * whether the crop should die.
     *
     * @param context The {@link Context} in which the requirements are evaluated.
     * @return True if all requirements are satisfied; false otherwise.
     */
    public boolean isMet(Context<CustomCropsBlockState> context) {
        return RequirementManager.isSatisfied(context, requirements);
    }

    /**
     * Retrieves the existence form that the crop should take after it dies.
     *
     * @return The {@link ExistenceForm} representing the state of the crop after death.
     */
    public ExistenceForm existenceForm() {
        return existenceForm;
    }
}