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

package net.momirealms.customcrops.api.core.mechanic.wateringcan;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.misc.water.FillMethod;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the configuration settings for a watering can in the CustomCrops plugin
 */
public interface WateringCanConfig {

    /**
     * Gets the unique identifier for this watering can configuration.
     *
     * @return The ID of the watering can configuration.
     */
    String id();

    /**
     * Gets the item ID representing the watering can.
     *
     * @return The item ID.
     */
    String itemID();

    /**
     * Gets the width of the watering area.
     *
     * @return The width of the watering area.
     */
    int width();

    /**
     * Gets the length of the watering area.
     *
     * @return The length of the watering area.
     */
    int length();

    /**
     * Gets the maximum water storage capacity of the watering can.
     *
     * @return The water storage capacity.
     */
    int storage();

    /**
     * Gets the amount of water added to pots per watering action.
     *
     * @return The watering amount.
     */
    int wateringAmount();

    /**
     * Determines if the lore (description text) of the watering can is dynamic.
     *
     * @return true if the lore is dynamic, false otherwise.
     */
    boolean dynamicLore();

    /**
     * Gets the set of pot IDs that the watering can is whitelisted to interact with.
     *
     * @return A set of whitelisted pot IDs.
     */
    Set<String> whitelistPots();

    /**
     * Gets the set of sprinkler IDs that the watering can is whitelisted to interact with.
     *
     * @return A set of whitelisted sprinkler IDs.
     */
    Set<String> whitelistSprinklers();

    /**
     * Gets the lore (description text) of the watering can.
     *
     * @return A list of {@link TextValue} objects representing the lore.
     */
    List<TextValue<Player>> lore();

    /**
     * Gets the water bar that manages the watering can's water level.
     *
     * @return The {@link WaterBar} instance.
     */
    WaterBar waterBar();

    /**
     * Gets the requirements for using the watering can.
     *
     * @return An array of {@link Requirement} instances for usage.
     */
    Requirement<Player>[] requirements();

    /**
     * Checks if the watering can has infinite water capacity.
     *
     * @return true if the watering can is infinite, false otherwise.
     */
    boolean infinite();

    /**
     * Gets the appearance of the watering can based on its water level.
     *
     * @param water The current water level.
     * @return The appearance value corresponding to the water level.
     */
    Integer appearance(int water);

    /**
     * Gets the actions performed when the watering can is full.
     *
     * @return An array of full {@link Action}s.
     */
    Action<Player>[] fullActions();

    /**
     * Gets the actions performed when water is added to the watering can.
     *
     * @return An array of add water {@link Action}s.
     */
    Action<Player>[] addWaterActions();

    /**
     * Gets the actions performed when water is consumed from the watering can.
     *
     * @return An array of consume water {@link Action}s.
     */
    Action<Player>[] consumeWaterActions();

    /**
     * Gets the actions performed when the watering can runs out of water.
     *
     * @return An array of run out of water {@link Action}s.
     */
    Action<Player>[] runOutOfWaterActions();

    /**
     * Gets the actions performed when the watering can is used on a wrong pot.
     *
     * @return An array of wrong pot {@link Action}s.
     */
    Action<Player>[] wrongPotActions();

    /**
     * Gets the actions performed when the watering can is used on a wrong sprinkler.
     *
     * @return An array of wrong sprinkler {@link Action}s.
     */
    Action<Player>[] wrongSprinklerActions();

    /**
     * Gets the methods available for filling the watering can with water.
     *
     * @return An array of {@link FillMethod}s.
     */
    FillMethod[] fillMethods();

    /**
     * Creates a new builder instance for constructing a {@link WateringCanConfig}.
     *
     * @return A new {@link Builder} instance.
     */
    static Builder builder() {
        return new WateringCanConfigImpl.BuilderImpl();
    }

    /**
     * Builder interface for constructing instances of {@link WateringCanConfig}
     */
    interface Builder {
        /**
         * Builds a new {@link WateringCanConfig} instance with the specified settings.
         *
         * @return A new {@link WateringCanConfig} instance.
         */
        WateringCanConfig build();

        /**
         * Sets the unique identifier for the watering can configuration.
         *
         * @param id The unique ID for the watering can.
         * @return The current instance of the Builder.
         */
        Builder id(String id);

        /**
         * Sets the item ID representing the watering can.
         *
         * @param itemID The item ID.
         * @return The current instance of the Builder.
         */
        Builder itemID(String itemID);

        /**
         * Sets the width of the watering area for the watering can.
         *
         * @param width The width of the watering area.
         * @return The current instance of the Builder.
         */
        Builder width(int width);

        /**
         * Sets the length of the watering area for the watering can.
         *
         * @param length The length of the watering area.
         * @return The current instance of the Builder.
         */
        Builder length(int length);

        /**
         * Sets the maximum water storage capacity of the watering can.
         *
         * @param storage The maximum amount of water the watering can can store.
         * @return The current instance of the Builder.
         */
        Builder storage(int storage);

        /**
         * Sets the amount of water added to pots per watering action.
         *
         * @param wateringAmount The amount of water consumed per use.
         * @return The current instance of the Builder.
         */
        Builder wateringAmount(int wateringAmount);

        /**
         * Sets whether the lore (description text) of the watering can is dynamic.
         *
         * @param dynamicLore True if the lore is dynamic, false otherwise.
         * @return The current instance of the Builder.
         */
        Builder dynamicLore(boolean dynamicLore);

        /**
         * Sets the whitelist of pot IDs that the watering can can interact with.
         *
         * @param whitelistPots A set of pot IDs that the watering can is allowed to interact with.
         * @return The current instance of the Builder.
         */
        Builder potWhitelist(Set<String> whitelistPots);

        /**
         * Sets the whitelist of sprinkler IDs that the watering can can interact with.
         *
         * @param whitelistSprinklers A set of sprinkler IDs that the watering can is allowed to interact with.
         * @return The current instance of the Builder.
         */
        Builder sprinklerWhitelist(Set<String> whitelistSprinklers);

        /**
         * Sets the lore (description text) for the watering can.
         *
         * @param lore A list of {@link TextValue} objects representing the lore text.
         * @return The current instance of the Builder.
         */
        Builder lore(List<TextValue<Player>> lore);

        /**
         * Sets the water bar that indicates the watering can's current water level.
         *
         * @param waterBar The {@link WaterBar} instance.
         * @return The current instance of the Builder.
         */
        Builder waterBar(WaterBar waterBar);

        /**
         * Sets the requirements that must be met to use the watering can.
         *
         * @param requirements An array of {@link Requirement} instances.
         * @return The current instance of the Builder.
         */
        Builder requirements(Requirement<Player>[] requirements);

        /**
         * Sets whether the watering can has infinite water capacity.
         *
         * @param infinite True if the watering can has infinite capacity, false otherwise.
         * @return The current instance of the Builder.
         */
        Builder infinite(boolean infinite);

        /**
         * Sets the appearances of the watering can at different water levels.
         *
         * @param appearances A map where keys are water levels and values are appearance custom model IDs.
         * @return The current instance of the Builder.
         */
        Builder appearances(Map<Integer, Integer> appearances);

        /**
         * Sets the actions that are triggered when the watering can is full of water.
         *
         * @param fullActions An array of {@link Action} instances to be performed when the can is full.
         * @return The current instance of the Builder.
         */
        Builder fullActions(Action<Player>[] fullActions);

        /**
         * Sets the actions that are triggered when water is added to the watering can.
         *
         * @param addWaterActions An array of {@link Action} instances to be performed when water is added.
         * @return The current instance of the Builder.
         */
        Builder addWaterActions(Action<Player>[] addWaterActions);

        /**
         * Sets the actions that are triggered when water is consumed from the watering can.
         *
         * @param consumeWaterActions An array of {@link Action} instances to be performed when water is consumed.
         * @return The current instance of the Builder.
         */
        Builder consumeWaterActions(Action<Player>[] consumeWaterActions);

        /**
         * Sets the actions that are triggered when the watering can runs out of water.
         *
         * @param runOutOfWaterActions An array of {@link Action} instances to be performed when the can is empty.
         * @return The current instance of the Builder.
         */
        Builder runOutOfWaterActions(Action<Player>[] runOutOfWaterActions);

        /**
         * Sets the actions that are triggered when the watering can is used on the wrong pot.
         *
         * @param wrongPotActions An array of {@link Action} instances to be performed on wrong pot usage.
         * @return The current instance of the Builder.
         */
        Builder wrongPotActions(Action<Player>[] wrongPotActions);

        /**
         * Sets the actions that are triggered when the watering can is used on the wrong sprinkler.
         *
         * @param wrongSprinklerActions An array of {@link Action} instances to be performed on wrong sprinkler usage.
         * @return The current instance of the Builder.
         */
        Builder wrongSprinklerActions(Action<Player>[] wrongSprinklerActions);

        /**
         * Sets the methods available for filling the watering can with water.
         *
         * @param fillMethods An array of {@link FillMethod} instances.
         * @return The current instance of the Builder.
         */
        Builder fillMethods(FillMethod[] fillMethods);
    }
}
