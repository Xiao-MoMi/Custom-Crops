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

package net.momirealms.customcrops.api.core.mechanic.pot;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Represents the configuration settings for a pot in the CustomCrops plugin.
 */
public interface PotConfig {

    /**
     * Gets the unique identifier for this pot configuration.
     *
     * @return The ID of the pot configuration.
     */
    String id();

    /**
     * Gets the maximum water storage capacity of the pot.
     *
     * @return The water storage capacity.
     */
    int storage();

    /**
     * Checks if the pot can accept rain as a source of water.
     *
     * @return True if rain is accepted, false otherwise.
     */
    boolean isRainDropAccepted();

    /**
     * Checks if the pot can accept water from nearby water sources.
     *
     * @return True if nearby water is accepted, false otherwise.
     */
    boolean isNearbyWaterAccepted();

    /**
     * Checks if the pot works as a vanilla farmland
     *
     * @return True if disabled
     */
    boolean disablePluginMechanism();

    /**
     * Should the pot ignore scheduled tick?
     *
     * @return ignore or not
     */
    boolean ignoreScheduledTick();

    /**
     * Should the pot ignore random tick?
     *
     * @return ignore or not
     */
    boolean ignoreRandomTick();

    /**
     * Gets the methods available for watering the pot.
     *
     * @return An array of {@link WateringMethod} instances.
     */
    WateringMethod[] wateringMethods();

    /**
     * Gets the set of block IDs associated with this pot.
     *
     * @return A set of block IDs.
     */
    Set<String> blocks();

    /**
     * Checks if a specific block ID is considered wet.
     *
     * @param blockID The block ID to check.
     * @return True if the block is wet, false otherwise.
     */
    boolean isWet(String blockID);

    /**
     * Gets the water bar that manages the pot's water level.
     *
     * @return The {@link WaterBar} instance.
     */
    WaterBar waterBar();

    /**
     * Gets the maximum number of fertilizers that can be added to the pot.
     *
     * @return The maximum number of fertilizers.
     */
    int maxFertilizers();

    /**
     * Gets the appearance of the pot based on its water state and fertilizer type.
     *
     * @param watered Whether the pot is watered.
     * @param type The fertilizer type.
     * @return The appearance ID of the pot.
     */
    String getPotAppearance(boolean watered, FertilizerType type);

    /**
     * Gets the requirements that must be met to place the pot.
     *
     * @return An array of {@link Requirement} instances for placement.
     */
    Requirement<Player>[] placeRequirements();

    /**
     * Gets the requirements that must be met to break the pot.
     *
     * @return An array of {@link Requirement} instances for breaking.
     */
    Requirement<Player>[] breakRequirements();

    /**
     * Gets the requirements that must be met to use the pot.
     *
     * @return An array of {@link Requirement} instances for usage.
     */
    Requirement<Player>[] useRequirements();

    /**
     * Gets the actions performed during each tick of the pot.
     *
     * @return An array of tick {@link Action} instances.
     */
    Action<CustomCropsBlockState>[] tickActions();

    /**
     * Gets the actions performed when the pot reaches its limit.
     *
     * @return An array of reach limit {@link Action} instances.
     */
    Action<Player>[] reachLimitActions();

    /**
     * Gets the actions performed when the pot is interacted with.
     *
     * @return An array of interact {@link Action} instances.
     */
    Action<Player>[] interactActions();

    /**
     * Gets the actions performed when the pot is placed.
     *
     * @return An array of place {@link Action} instances.
     */
    Action<Player>[] placeActions();

    /**
     * Gets the actions performed when the pot is broken.
     *
     * @return An array of break {@link Action} instances.
     */
    Action<Player>[] breakActions();

    /**
     * Gets the actions performed when water is added to the pot.
     *
     * @return An array of add water {@link Action} instances.
     */
    Action<Player>[] addWaterActions();

    /**
     * Gets the actions performed when the pot is full of water.
     *
     * @return An array of full water {@link Action} instances.
     */
    Action<Player>[] fullWaterActions();

    /**
     * Gets the actions performed when the pot reaches its maximum fertilizer capacity.
     *
     * @return An array of max fertilizer {@link Action} instances.
     */
    Action<Player>[] maxFertilizerActions();

    /**
     * Creates a new builder instance for constructing a {@link PotConfig}.
     *
     * @return A new {@link Builder} instance.
     */
    static Builder builder() {
        return new PotConfigImpl.BuilderImpl();
    }

    /**
     * Builder interface for constructing instances of {@link PotConfig}.
     */
    interface Builder {

        /**
         * Builds a new {@link PotConfig} instance with the specified settings.
         *
         * @return A new {@link PotConfig} instance.
         */
        PotConfig build();

        /**
         * Sets the unique identifier for the pot configuration.
         *
         * @param id The unique ID for the pot.
         * @return The current instance of the Builder.
         */
        Builder id(String id);

        /**
         * Sets the maximum water storage capacity of the pot.
         *
         * @param storage The maximum amount of water the pot can store.
         * @return The current instance of the Builder.
         */
        Builder storage(int storage);

        /**
         * Sets if the pot works as a vanilla farmland
         *
         * @param vanillaFarmland if works as a vanilla farmland
         * @return The current instance of the Builder.
         */
        Builder vanillaFarmland(boolean vanillaFarmland);

        /**
         * Mark this pot as a vanilla one and disable plugin mechanisms
         *
         * @param vanillaPots pots
         * @return The current instance of the Builder.
         */
        Builder vanillaPots(List<String> vanillaPots);

        /**
         * Sets whether the pot can accept rain as a source of water.
         *
         * @param isRainDropAccepted True if rain is accepted, false otherwise.
         * @return The current instance of the Builder.
         */
        Builder isRainDropAccepted(boolean isRainDropAccepted);

        /**
         * Sets whether the pot can accept water from nearby water sources.
         *
         * @param isNearbyWaterAccepted True if nearby water is accepted, false otherwise.
         * @return The current instance of the Builder.
         */
        Builder isNearbyWaterAccepted(boolean isNearbyWaterAccepted);

        /**
         * Sets whether the pot ignores random tick
         *
         * @param ignoreRandomTick True if ignore random tick
         * @return The current instance of the Builder.
         */
        Builder ignoreRandomTick(boolean ignoreRandomTick);

        /**
         * Sets whether the pot ignores scheduled tick
         *
         * @param ignoreScheduledTick True if ignore scheduled tick
         * @return The current instance of the Builder.
         */
        Builder ignoreScheduledTick(boolean ignoreScheduledTick);

        /**
         * Sets the methods available for watering the pot.
         *
         * @param wateringMethods An array of {@link WateringMethod} instances.
         * @return The current instance of the Builder.
         */
        Builder wateringMethods(WateringMethod[] wateringMethods);

        /**
         * Sets the water bar that indicates the pot's current water level.
         *
         * @param waterBar The {@link WaterBar} instance.
         * @return The current instance of the Builder.
         */
        Builder waterBar(WaterBar waterBar);

        /**
         * Sets the maximum number of fertilizers that can be added to the pot.
         *
         * @param maxFertilizers The maximum number of fertilizers.
         * @return The current instance of the Builder.
         */
        Builder maxFertilizers(int maxFertilizers);

        /**
         * Sets the requirements that must be met to place the pot.
         *
         * @param requirements An array of {@link Requirement} instances.
         * @return The current instance of the Builder.
         */
        Builder placeRequirements(Requirement<Player>[] requirements);

        /**
         * Sets the requirements that must be met to break the pot.
         *
         * @param requirements An array of {@link Requirement} instances.
         * @return The current instance of the Builder.
         */
        Builder breakRequirements(Requirement<Player>[] requirements);

        /**
         * Sets the requirements that must be met to use the pot.
         *
         * @param requirements An array of {@link Requirement} instances.
         * @return The current instance of the Builder.
         */
        Builder useRequirements(Requirement<Player>[] requirements);

        /**
         * Sets the actions performed during each tick of the pot.
         *
         * @param tickActions An array of tick {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder tickActions(Action<CustomCropsBlockState>[] tickActions);

        /**
         * Sets the actions performed when the pot reaches its limit.
         *
         * @param reachLimitActions An array of reach limit {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder reachLimitActions(Action<Player>[] reachLimitActions);

        /**
         * Sets the actions performed when the pot is interacted with.
         *
         * @param interactActions An array of interact {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder interactActions(Action<Player>[] interactActions);

        /**
         * Sets the actions performed when the pot is placed.
         *
         * @param placeActions An array of place {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder placeActions(Action<Player>[] placeActions);

        /**
         * Sets the actions performed when the pot is broken.
         *
         * @param breakActions An array of break {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder breakActions(Action<Player>[] breakActions);

        /**
         * Sets the actions performed when water is added to the pot.
         *
         * @param addWaterActions An array of add water {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder addWaterActions(Action<Player>[] addWaterActions);

        /**
         * Sets the actions performed when the pot is full of water.
         *
         * @param fullWaterActions An array of full water {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder fullWaterActions(Action<Player>[] fullWaterActions);

        /**
         * Sets the actions performed when the pot reaches its maximum fertilizer capacity.
         *
         * @param maxFertilizerActions An array of max fertilizer {@link Action} instances.
         * @return The current instance of the Builder.
         */
        Builder maxFertilizerActions(Action<Player>[] maxFertilizerActions);

        /**
         * Sets the basic appearance of the pot.
         *
         * @param basicAppearance A {@link Pair} representing the basic appearance of the pot.
         * @return The current instance of the Builder.
         */
        Builder basicAppearance(Pair<String, String> basicAppearance);

        /**
         * Sets the appearance map of the pot for different fertilizer types.
         *
         * @param potAppearanceMap A map of {@link FertilizerType} to {@link Pair} of appearance strings.
         * @return The current instance of the Builder.
         */
        Builder potAppearanceMap(HashMap<FertilizerType, Pair<String, String>> potAppearanceMap);
    }
}
