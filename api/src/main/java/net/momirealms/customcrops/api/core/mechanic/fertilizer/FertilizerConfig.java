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

package net.momirealms.customcrops.api.core.mechanic.fertilizer;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Represents the configuration settings for a fertilizer in the CustomCrops plugin.
 */
public interface FertilizerConfig {

    /**
     * Gets the unique identifier for this fertilizer configuration.
     *
     * @return The unique ID of the fertilizer as a {@link String}.
     */
    String id();

    /**
     * Gets the type of the fertilizer.
     *
     * @return The {@link FertilizerType} of this fertilizer.
     */
    FertilizerType type();

    /**
     * Determines whether the fertilizer should be used before planting crops.
     *
     * @return True if the fertilizer is applied before planting, false otherwise.
     */
    boolean beforePlant();

    /**
     * Gets the icon representation for this fertilizer.
     *
     * @return The icon as a {@link String}, typically an item or block ID.
     */
    String icon();

    /**
     * Retrieves the requirements for using this fertilizer.
     *
     * @return An array of {@link Requirement} objects that must be met to use the fertilizer.
     */
    Requirement<Player>[] requirements();

    /**
     * Gets the item ID associated with this fertilizer.
     *
     * @return The item ID as a {@link String}.
     */
    String itemID();

    /**
     * Gets the number of times this fertilizer can be used.
     *
     * @return The number of usages available.
     */
    int times();

    /**
     * Gets the set of pot IDs that this fertilizer is allowed to be used with.
     *
     * @return A set of pot IDs as {@link String} objects.
     */
    Set<String> whitelistPots();

    /**
     * Retrieves the actions to be performed before planting when using this fertilizer.
     *
     * @return An array of {@link Action} objects representing the actions to perform.
     */
    Action<Player>[] beforePlantActions();

    /**
     * Retrieves the actions to be performed when the fertilizer is used.
     *
     * @return An array of {@link Action} objects representing the actions to perform upon use.
     */
    Action<Player>[] useActions();

    /**
     * Retrieves the actions to be performed when the fertilizer is used on the wrong type of pot.
     *
     * @return An array of {@link Action} objects representing the actions to perform for wrong pot usage.
     */
    Action<Player>[] wrongPotActions();

    /**
     * Processes and potentially modifies the gain points based on this fertilizer's effects.
     *
     * @param previousPoints The points before processing.
     * @return The modified points after applying the fertilizer's effect.
     */
    int processGainPoints(int previousPoints);

    /**
     * Processes and potentially modifies the amount of water to lose when this fertilizer is used.
     *
     * @param waterToLose The amount of water to lose before processing.
     * @return The modified water loss amount after applying the fertilizer's effect.
     */
    int processWaterToLose(int waterToLose);

    /**
     * Processes and potentially modifies the variation chance for crop growth based on this fertilizer's effects.
     *
     * @param previousChance The variation chance before processing.
     * @return The modified variation chance after applying the fertilizer's effect.
     */
    double processVariationChance(double previousChance);

    /**
     * Processes and potentially modifies the amount of items dropped when the crop is harvested.
     *
     * @param amount The amount of items to drop before processing.
     * @return The modified drop amount after applying the fertilizer's effect.
     */
    int processDroppedItemAmount(int amount);

    /**
     * Provides an optional override for the quality ratio of crops affected by this fertilizer.
     * This method may return null if there is no override.
     *
     * @return An array of doubles representing the quality ratio override, or null if not applicable.
     */
    double[] overrideQualityRatio();
}