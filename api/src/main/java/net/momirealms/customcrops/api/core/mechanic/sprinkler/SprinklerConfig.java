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
package net.momirealms.customcrops.api.core.mechanic.sprinkler;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Represents the configuration settings for a sprinkler within the CustomCrops plugin.
 */
public interface SprinklerConfig {

    /**
     * Gets the unique identifier for this sprinkler configuration.
     *
     * @return The ID of the sprinkler configuration.
     */
    String id();

    /**
     * Gets the maximum water storage capacity of the sprinkler.
     *
     * @return The water storage capacity.
     */
    int storage();

    /**
     * Gets the range of the sprinkler's watering area.
     *
     * @return A 2D array representing the offsets of the target blocks.
     */
    int[][] range();

    /**
     * Checks if the sprinkler has infinite water capacity.
     *
     * @return true if the sprinkler is infinite, false otherwise.
     */
    boolean infinite();

    /**
     * Gets the amount of water the sprinkler adds to pot per operation.
     *
     * @return The sprinkling amount.
     */
    int wateringAmount();

    /**
     * Gets the amount of water sprinkled per operation.
     *
     * @return The sprinkling amount.
     */
    int sprinklingAmount();

    /**
     * Gets the 2D item representation of the sprinkler.
     *
     * @return The 2D item ID, or null if not applicable.
     */
    @Nullable
    String twoDItem();

    /**
     * Gets the 3D item representation of the sprinkler without water.
     *
     * @return The 3D item ID without water.
     */
    @NotNull
    String threeDItem();

    /**
     * Gets the 3D item representation of the sprinkler with water.
     *
     * @return The 3D item ID with water.
     */
    @NotNull
    String threeDItemWithWater();

    /**
     * Gets the whitelist of pots that the sprinkler can interact with.
     *
     * @return A set of pot IDs that are whitelisted.
     */
    @NotNull
    Set<String> potWhitelist();

    /**
     * Gets the model IDs associated with this sprinkler.
     *
     * @return A set of model IDs.
     */
    @NotNull
    Set<String> modelIDs();

    /**
     * Gets the water bar that manages the sprinkler's water level.
     *
     * @return The {@link WaterBar}, or null if not applicable.
     */
    @Nullable
    WaterBar waterBar();

    /**
     * Gets the existence form of the sprinkler, which indicates how it exists in the world.
     *
     * @return The {@link ExistenceForm} of the sprinkler.
     */
    @NotNull
    ExistenceForm existenceForm();

    /**
     * Gets the tick requirements for the sprinkler.
     *
     * @return An array of tick {@link Requirement}s, or null if none.
     */
    @Nullable
    Requirement<CustomCropsBlockState>[] tickRequirements();

    /**
     * Gets the placement requirements for the sprinkler.
     *
     * @return An array of placement {@link Requirement}s, or null if none.
     */
    @Nullable
    Requirement<Player>[] placeRequirements();

    /**
     * Gets the breaking requirements for the sprinkler.
     *
     * @return An array of breaking {@link Requirement}s, or null if none.
     */
    @Nullable
    Requirement<Player>[] breakRequirements();

    /**
     * Gets the usage requirements for the sprinkler.
     *
     * @return An array of usage {@link Requirement}s, or null if none.
     */
    @Nullable
    Requirement<Player>[] useRequirements();

    /**
     * Gets the actions performed when the sprinkler is working.
     *
     * @return An array of work {@link Action}s, or null if none.
     */
    @Nullable
    Action<CustomCropsBlockState>[] workActions();

    /**
     * Gets the actions performed when the sprinkler is interacted with.
     *
     * @return An array of interact {@link Action}s, or null if none.
     */
    @Nullable
    Action<Player>[] interactActions();

    /**
     * Gets the actions performed when the sprinkler is placed.
     *
     * @return An array of place {@link Action}s, or null if none.
     */
    @Nullable
    Action<Player>[] placeActions();

    /**
     * Gets the actions performed when the sprinkler is broken.
     *
     * @return An array of break {@link Action}s, or null if none.
     */
    @Nullable
    Action<Player>[] breakActions();

    /**
     * Gets the actions performed when water is added to the sprinkler.
     *
     * @return An array of add water {@link Action}s, or null if none.
     */
    @Nullable
    Action<Player>[] addWaterActions();

    /**
     * Gets the actions performed when the sprinkler reaches its water limit.
     *
     * @return An array of reach limit {@link Action}s, or null if none.
     */
    @Nullable
    Action<Player>[] reachLimitActions();

    /**
     * Gets the actions performed when the sprinkler's water level is full.
     *
     * @return An array of full water {@link Action}s, or null if none.
     */
    @Nullable
    Action<Player>[] fullWaterActions();

    /**
     * Gets the methods used for watering by the sprinkler.
     *
     * @return An array of {@link WateringMethod}s.
     */
    @NotNull
    WateringMethod[] wateringMethods();

    /**
     * Creates a new builder instance for constructing a {@link SprinklerConfig}.
     *
     * @return A new {@link Builder} instance.
     */
    static Builder builder() {
        return new SprinklerConfigImpl.BuilderImpl();
    }

    /**
     * Builder interface for constructing instances of {@link SprinklerConfig}.
     */
    interface Builder {

        /**
         * Builds a new {@link SprinklerConfig} instance with the specified settings.
         *
         * @return A new {@link SprinklerConfig} instance.
         */
        SprinklerConfig build();

        /**
         * Sets the unique identifier for the sprinkler configuration.
         *
         * @param id The unique ID for the sprinkler.
         * @return The current instance of the Builder.
         */
        Builder id(String id);

        /**
         * Sets the existence form of the sprinkler, indicating its form in the world.
         *
         * @param existenceForm The existence form of the sprinkler.
         * @return The current instance of the Builder.
         */
        Builder existenceForm(ExistenceForm existenceForm);

        /**
         * Sets the water storage capacity of the sprinkler.
         *
         * @param storage The maximum water storage capacity.
         * @return The current instance of the Builder.
         */
        Builder storage(int storage);

        /**
         * Sets the range of the sprinkler's watering area.
         *
         * @param range A 2D array defining the range.
         * @return The current instance of the Builder.
         */
        Builder range(int[][] range);

        /**
         * Specifies whether the sprinkler has an infinite water supply.
         *
         * @param infinite true if the sprinkler has infinite water; false otherwise.
         * @return The current instance of the Builder.
         */
        Builder infinite(boolean infinite);

        /**
         * Sets the amount of water the sprinkler adds to pot per operation.
         *
         * @param wateringAmount The amount of water used per sprinkling.
         * @return The current instance of the Builder.
         */
        Builder wateringAmount(int wateringAmount);

        /**
         * Sets the amount of water the sprinkler uses per operation.
         *
         * @param sprinklingAmount The amount of water used per sprinkling.
         * @return The current instance of the Builder.
         */
        Builder sprinklingAmount(int sprinklingAmount);

        /**
         * Sets the whitelist of pot IDs that the sprinkler can interact with.
         *
         * @param potWhitelist A set of whitelisted pot IDs.
         * @return The current instance of the Builder.
         */
        Builder potWhitelist(Set<String> potWhitelist);

        /**
         * Sets the water bar that manages the sprinkler's water level.
         *
         * @param waterBar The {@link WaterBar} instance.
         * @return The current instance of the Builder.
         */
        Builder waterBar(WaterBar waterBar);

        /**
         * Sets the 2D item representation of the sprinkler.
         *
         * @param twoDItem The ID of the 2D item representation.
         * @return The current instance of the Builder.
         */
        Builder twoDItem(@Nullable String twoDItem);

        /**
         * Sets the 3D item representation of the sprinkler when it does not contain water.
         *
         * @param threeDItem The ID of the 3D item without water.
         * @return The current instance of the Builder.
         */
        Builder threeDItem(String threeDItem);

        /**
         * Sets the 3D item representation of the sprinkler when it contains water.
         *
         * @param threeDItemWithWater The ID of the 3D item with water.
         * @return The current instance of the Builder.
         */
        Builder threeDItemWithWater(String threeDItemWithWater);

        /**
         * Sets the requirements for placing the sprinkler.
         *
         * @param placeRequirements An array of {@link Requirement} instances for placement.
         * @return The current instance of the Builder.
         */
        Builder placeRequirements(Requirement<Player>[] placeRequirements);

        /**
         * Sets the requirements for breaking the sprinkler.
         *
         * @param breakRequirements An array of {@link Requirement} instances for breaking.
         * @return The current instance of the Builder.
         */
        Builder breakRequirements(Requirement<Player>[] breakRequirements);

        /**
         * Sets the requirements for using the sprinkler.
         *
         * @param useRequirements An array of {@link Requirement} instances for usage.
         * @return The current instance of the Builder.
         */
        Builder useRequirements(Requirement<Player>[] useRequirements);

        /**
         * Sets the requirements for the sprinkler to tick.
         *
         * @param tickRequirements An array of {@link Requirement} instances for ticking.
         * @return The current instance of the Builder.
         */
        Builder tickRequirements(Requirement<CustomCropsBlockState>[] tickRequirements);

        /**
         * Sets the actions to be performed when the sprinkler is working.
         *
         * @param workActions An array of {@link Action} instances for working.
         * @return The current instance of the Builder.
         */
        Builder workActions(Action<CustomCropsBlockState>[] workActions);

        /**
         * Sets the actions to be performed when the sprinkler is interacted with.
         *
         * @param interactActions An array of {@link Action} instances for interaction.
         * @return The current instance of the Builder.
         */
        Builder interactActions(Action<Player>[] interactActions);

        /**
         * Sets the actions to be performed when water is added to the sprinkler.
         *
         * @param addWaterActions An array of {@link Action} instances for adding water.
         * @return The current instance of the Builder.
         */
        Builder addWaterActions(Action<Player>[] addWaterActions);

        /**
         * Sets the actions to be performed when the sprinkler reaches its water limit.
         *
         * @param reachLimitActions An array of {@link Action} instances for reaching the limit.
         * @return The current instance of the Builder.
         */
        Builder reachLimitActions(Action<Player>[] reachLimitActions);

        /**
         * Sets the actions to be performed when the sprinkler is placed.
         *
         * @param placeActions An array of {@link Action} instances for placing.
         * @return The current instance of the Builder.
         */
        Builder placeActions(Action<Player>[] placeActions);

        /**
         * Sets the actions to be performed when the sprinkler is broken.
         *
         * @param breakActions An array of {@link Action} instances for breaking.
         * @return The current instance of the Builder.
         */
        Builder breakActions(Action<Player>[] breakActions);

        /**
         * Sets the actions to be performed when the sprinkler's water level is full.
         *
         * @param fullWaterActions An array of {@link Action} instances for full water.
         * @return The current instance of the Builder.
         */
        Builder fullWaterActions(Action<Player>[] fullWaterActions);

        /**
         * Sets the watering methods that the sprinkler can use.
         *
         * @param wateringMethods An array of {@link WateringMethod} instances.
         * @return The current instance of the Builder.
         */
        Builder wateringMethods(WateringMethod[] wateringMethods);
    }
}
