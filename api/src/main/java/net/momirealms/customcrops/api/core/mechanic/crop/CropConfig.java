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
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents the configuration settings for a crop in the CustomCrops plugin.
 */
public interface CropConfig {

    /**
     * Gets the unique identifier for this crop configuration.
     *
     * @return The unique ID of the crop as a {@link String}.
     */
    String id();

    /**
     * Gets the seed item ID associated with this crop.
     *
     * @return The seed item ID as a {@link String}.
     */
    String seed();

    /**
     * Gets the maximum growth points for this crop.
     *
     * @return The maximum points as an integer.
     */
    int maxPoints();

    /**
     * Retrieves the requirements that must be met to plant this crop.
     *
     * @return An array of {@link Requirement} objects for planting the crop.
     */
    Requirement<Player>[] plantRequirements();

    /**
     * Retrieves the requirements that must be met to break this crop.
     *
     * @return An array of {@link Requirement} objects for breaking the crop.
     */
    Requirement<Player>[] breakRequirements();

    /**
     * Retrieves the requirements that must be met to interact with this crop.
     *
     * @return An array of {@link Requirement} objects for interacting with the crop.
     */
    Requirement<Player>[] interactRequirements();

    /**
     * Retrieves the growth conditions that must be met for this crop to grow.
     *
     * @return An array of {@link GrowCondition} objects for crop growth.
     */
    GrowCondition[] growConditions();

    /**
     * Retrieves the actions to be performed when the crop is placed in the wrong pot.
     *
     * @return An array of {@link Action} objects for wrong pot usage.
     */
    Action<Player>[] wrongPotActions();

    /**
     * Retrieves the actions to be performed when interacting with the crop.
     *
     * @return An array of {@link Action} objects for crop interaction.
     */
    Action<Player>[] interactActions();

    /**
     * Retrieves the actions to be performed when breaking the crop.
     *
     * @return An array of {@link Action} objects for breaking the crop.
     */
    Action<Player>[] breakActions();

    /**
     * Retrieves the actions to be performed when planting the crop.
     *
     * @return An array of {@link Action} objects for planting the crop.
     */
    Action<Player>[] plantActions();

    /**
     * Retrieves the actions to be performed when the crop reaches its growth limit.
     *
     * @return An array of {@link Action} objects for reaching the growth limit.
     */
    Action<Player>[] reachLimitActions();

    /**
     * Retrieves the actions to be performed when the crop dies.
     *
     * @return An array of {@link Action} objects for crop death.
     */
    Action<CustomCropsBlockState>[] deathActions();

    /**
     * Retrieves the conditions that determine when the crop dies.
     *
     * @return An array of {@link DeathCondition} objects for crop death.
     */
    DeathCondition[] deathConditions();

    /**
     * Retrieves the bone meal effects that can be applied to this crop.
     *
     * @return An array of {@link BoneMeal} objects representing bone meal effects.
     */
    BoneMeal[] boneMeals();

    /**
     * Indicates whether the crop should consider rotation during placement.
     *
     * @return True if rotation is considered, false otherwise.
     */
    boolean rotation();

    /**
     * Gets the set of pot IDs that this crop is allowed to be planted in.
     *
     * @return A set of pot IDs as {@link String} objects.
     */
    Set<String> potWhitelist();

    /**
     * Gets the crop stage configuration based on the growth point value.
     *
     * @param point The growth points to check.
     * @return The {@link CropStageConfig} corresponding to the provided point.
     */
    CropStageConfig stageByPoint(int point);

    /**
     * Gets the crop stage configuration based on a stage model identifier.
     *
     * @param stageModel The stage model identifier.
     * @return The {@link CropStageConfig} corresponding to the provided model ID, or null if not found.
     */
    @Nullable
    CropStageConfig stageByID(String stageModel);

    /**
     * Gets the crop stage configuration with a model based on the growth point value.
     *
     * @param point The growth points to check.
     * @return The {@link CropStageConfig} with a model corresponding to the provided point.
     */
    CropStageConfig stageWithModelByPoint(int point);

    /**
     * Retrieves all the crop stage configurations for this crop.
     *
     * @return A collection of {@link CropStageConfig} objects representing all stages.
     */
    Collection<CropStageConfig> stages();

    /**
     * Retrieves all the crop stage IDs for this crop.
     *
     * @return A collection of stage IDs as {@link String} objects.
     */
    Collection<String> stageIDs();

    /**
     * Gets the closest lower or equal stage configuration based on the provided growth points.
     *
     * @param previousPoint The growth points to check.
     * @return A {@link Map.Entry} containing the point and corresponding {@link CropStageConfig}.
     */
    Map.Entry<Integer, CropStageConfig> getFloorStageEntry(int previousPoint);

    /**
     * Creates a new builder instance for constructing a {@link CropConfig}.
     *
     * @return A new {@link Builder} instance.
     */
    static Builder builder() {
        return new CropConfigImpl.BuilderImpl();
    }

    /**
     * Should the crop ignore scheduled tick?
     *
     * @return ignore or not
     */
    boolean ignoreScheduledTick();

    /**
     * Should the crop ignore random tick?
     *
     * @return ignore or not
     */
    boolean ignoreRandomTick();

    /**
     * Builder interface for constructing instances of {@link CropConfig}.
     */
    interface Builder {

        /**
         * Builds a new {@link CropConfig} instance with the specified settings.
         *
         * @return A new {@link CropConfig} instance.
         */
        CropConfig build();

        /**
         * Sets the unique identifier for this crop configuration.
         *
         * @param id The unique ID of the crop.
         * @return The builder instance for chaining.
         */
        Builder id(String id);

        /**
         * Sets the seed item ID associated with this crop.
         *
         * @param seed The seed item ID.
         * @return The builder instance for chaining.
         */
        Builder seed(String seed);

        /**
         * Sets the maximum growth points for this crop.
         *
         * @param maxPoints The maximum points the crop can have.
         * @return The builder instance for chaining.
         */
        Builder maxPoints(int maxPoints);

        /**
         * Sets the actions to be performed when the crop is placed in the wrong pot.
         *
         * @param wrongPotActions An array of {@link Action} objects for wrong pot usage.
         * @return The builder instance for chaining.
         */
        Builder wrongPotActions(Action<Player>[] wrongPotActions);

        /**
         * Sets the actions to be performed when interacting with the crop.
         *
         * @param interactActions An array of {@link Action} objects for crop interaction.
         * @return The builder instance for chaining.
         */
        Builder interactActions(Action<Player>[] interactActions);

        /**
         * Sets the actions to be performed when breaking the crop.
         *
         * @param breakActions An array of {@link Action} objects for breaking the crop.
         * @return The builder instance for chaining.
         */
        Builder breakActions(Action<Player>[] breakActions);

        /**
         * Sets the actions to be performed when planting the crop.
         *
         * @param plantActions An array of {@link Action} objects for planting the crop.
         * @return The builder instance for chaining.
         */
        Builder plantActions(Action<Player>[] plantActions);

        /**
         * Sets the actions to be performed when the crop reaches its growth limit.
         *
         * @param reachLimitActions An array of {@link Action} objects for reaching the growth limit.
         * @return The builder instance for chaining.
         */
        Builder reachLimitActions(Action<Player>[] reachLimitActions);

        /**
         * Sets the requirements that must be met to plant this crop.
         *
         * @param plantRequirements An array of {@link Requirement} objects for planting the crop.
         * @return The builder instance for chaining.
         */
        Builder plantRequirements(Requirement<Player>[] plantRequirements);

        /**
         * Sets the requirements that must be met to break this crop.
         *
         * @param breakRequirements An array of {@link Requirement} objects for breaking the crop.
         * @return The builder instance for chaining.
         */
        Builder breakRequirements(Requirement<Player>[] breakRequirements);

        /**
         * Sets the requirements that must be met to interact with this crop.
         *
         * @param interactRequirements An array of {@link Requirement} objects for interacting with the crop.
         * @return The builder instance for chaining.
         */
        Builder interactRequirements(Requirement<Player>[] interactRequirements);

        /**
         * Sets the growth conditions that must be met for this crop to grow.
         *
         * @param growConditions An array of {@link GrowCondition} objects for crop growth.
         * @return The builder instance for chaining.
         */
        Builder growConditions(GrowCondition[] growConditions);

        /**
         * Sets the conditions that determine when the crop dies.
         *
         * @param deathConditions An array of {@link DeathCondition} objects for crop death.
         * @return The builder instance for chaining.
         */
        Builder deathConditions(DeathCondition[] deathConditions);

        /**
         * Sets the bone meal effects that can be applied to this crop.
         *
         * @param boneMeals An array of {@link BoneMeal} objects representing bone meal effects.
         * @return The builder instance for chaining.
         */
        Builder boneMeals(BoneMeal[] boneMeals);

        /**
         * Sets whether the crop should consider rotation during placement.
         *
         * @param rotation True if rotation is considered, false otherwise.
         * @return The builder instance for chaining.
         */
        Builder rotation(boolean rotation);

        /**
         * Sets the pot whitelist for this crop.
         * Only pots in this list will be allowed to plant the crop.
         *
         * @param whitelist A set of pot IDs that are allowed to plant this crop.
         * @return The builder instance for chaining.
         */
        Builder potWhitelist(Set<String> whitelist);

        /**
         * Sets the stages of the crop based on growth points.
         *
         * @param stages A collection of {@link CropStageConfig.Builder} objects representing crop stages.
         * @return The builder instance for chaining.
         */
        Builder stages(Collection<CropStageConfig.Builder> stages);

        /**
         * Sets whether the crop ignores random tick
         *
         * @param ignoreRandomTick True if ignore random tick
         * @return The current instance of the Builder.
         */
        Builder ignoreRandomTick(boolean ignoreRandomTick);

        /**
         * Sets whether the crop ignores scheduled tick
         *
         * @param ignoreScheduledTick True if ignore scheduled tick
         * @return The current instance of the Builder.
         */
        Builder ignoreScheduledTick(boolean ignoreScheduledTick);
    }
}
