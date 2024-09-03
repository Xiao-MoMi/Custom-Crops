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

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Interface representing the configuration of a specific stage of crop growth.
 */
public interface CropStageConfig {

    /**
     * Gets the parent {@link CropConfig} associated with this crop stage.
     *
     * @return The parent crop configuration.
     */
    CropConfig crop();

    /**
     * Gets the offset for displaying crop information.
     * This offset is used to adjust the display of information related to the crop stage.
     *
     * @return The display information offset.
     */
    double displayInfoOffset();

    /**
     * Gets the unique identifier for this crop stage.
     *
     * @return The stage ID, or null if not defined.
     */
    @Nullable
    String stageID();

    /**
     * Gets the growth point associated with this crop stage.
     * This point represents the growth progress of the crop.
     *
     * @return The growth point.
     */
    int point();

    /**
     * Gets the requirements that must be met to interact with the crop at this stage.
     *
     * @return An array of interaction requirements.
     */
    Requirement<Player>[] interactRequirements();

    /**
     * Gets the requirements that must be met to break the crop at this stage.
     *
     * @return An array of break requirements.
     */
    Requirement<Player>[] breakRequirements();

    /**
     * Gets the actions to be performed when interacting with the crop at this stage.
     *
     * @return An array of interaction actions.
     */
    Action<Player>[] interactActions();

    /**
     * Gets the actions to be performed when breaking the crop at this stage.
     *
     * @return An array of break actions.
     */
    Action<Player>[] breakActions();

    /**
     * Gets the actions to be performed when the crop grows to this stage.
     *
     * @return An array of grow actions.
     */
    Action<CustomCropsBlockState>[] growActions();

    /**
     * Gets the form of existence that this crop stage takes.
     *
     * @return The {@link ExistenceForm} of the crop stage.
     */
    ExistenceForm existenceForm();

    /**
     * Creates a new builder for constructing instances of {@link CropStageConfig}.
     *
     * @return A new {@link Builder} instance.
     */
    static Builder builder() {
        return new CropStageConfigImpl.BuilderImpl();
    }

    /**
     * Builder interface for constructing instances of {@link CropStageConfig}.
     */
    interface Builder {

        /**
         * Builds a new {@link CropStageConfig} instance with the specified settings.
         *
         * @return A new {@link CropStageConfig} instance.
         */
        CropStageConfig build();

        /**
         * Sets the parent crop configuration for this crop stage.
         *
         * @param crop The parent {@link CropConfig}.
         * @return The builder instance for chaining.
         */
        Builder crop(CropConfig crop);

        /**
         * Sets the display information offset for this crop stage.
         *
         * @param offset The display info offset.
         * @return The builder instance for chaining.
         */
        Builder displayInfoOffset(double offset);

        /**
         * Sets the unique identifier for this crop stage.
         *
         * @param id The stage ID.
         * @return The builder instance for chaining.
         */
        Builder stageID(String id);

        /**
         * Sets the growth point associated with this crop stage.
         *
         * @param i The growth point.
         * @return The builder instance for chaining.
         */
        Builder point(int i);

        /**
         * Sets the interaction requirements for this crop stage.
         *
         * @param requirements An array of interaction requirements.
         * @return The builder instance for chaining.
         */
        Builder interactRequirements(Requirement<Player>[] requirements);

        /**
         * Sets the break requirements for this crop stage.
         *
         * @param requirements An array of break requirements.
         * @return The builder instance for chaining.
         */
        Builder breakRequirements(Requirement<Player>[] requirements);

        /**
         * Sets the interaction actions for this crop stage.
         *
         * @param actions An array of interaction actions.
         * @return The builder instance for chaining.
         */
        Builder interactActions(Action<Player>[] actions);

        /**
         * Sets the break actions for this crop stage.
         *
         * @param actions An array of break actions.
         * @return The builder instance for chaining.
         */
        Builder breakActions(Action<Player>[] actions);

        /**
         * Sets the grow actions for this crop stage.
         *
         * @param actions An array of grow actions.
         * @return The builder instance for chaining.
         */
        Builder growActions(Action<CustomCropsBlockState>[] actions);

        /**
         * Sets the existence form of the crop for this crop stage.
         *
         * @param existenceForm The {@link ExistenceForm} of the crop.
         * @return The builder instance for chaining.
         */
        Builder existenceForm(ExistenceForm existenceForm);
    }
}
