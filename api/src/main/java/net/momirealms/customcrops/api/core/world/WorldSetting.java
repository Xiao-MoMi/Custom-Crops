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

package net.momirealms.customcrops.api.core.world;

/**
 * Represents the configuration settings for a CustomCrops world, including various
 * parameters for ticking behavior, season management, and random events.
 */
public class WorldSetting implements Cloneable {

    private final boolean enableScheduler;
    private final int minTickUnit;
    private final int tickCropInterval;
    private final int tickPotInterval;
    private final int tickSprinklerInterval;
    private final boolean offlineTick;
    private final boolean enableSeason;
    private final boolean autoSeasonChange;
    private final int seasonDuration;
    private final int cropPerChunk;
    private final int potPerChunk;
    private final int sprinklerPerChunk;
    private final int randomTickSpeed;
    private final int maxOfflineTime;
    private final int maxLoadingTime;
    private final int tickCropMode;
    private final int tickPotMode;
    private final int tickSprinklerMode;

    /**
     * Private constructor to initialize a WorldSetting instance with the provided parameters.
     *
     * @param enableScheduler       Whether the scheduler is enabled.
     * @param minTickUnit           The minimum unit of tick.
     * @param tickCropMode          The tick mode of crop
     * @param tickCropInterval      The interval for ticking crops.
     * @param tickPotMode           The tick mode of pot
     * @param tickPotInterval       The interval for ticking pots.
     * @param tickSprinklerMode     The tick mode of sprinkler
     * @param tickSprinklerInterval The interval for ticking sprinklers.
     * @param offlineTick           Whether offline ticking is enabled.
     * @param maxOfflineTime        The maximum offline time allowed.
     * @param maxLoadingTime        The maximum time allowed to load.
     * @param enableSeason          Whether seasons are enabled.
     * @param autoSeasonChange      Whether season change is automatic.
     * @param seasonDuration        The duration of each season.
     * @param cropPerChunk          The maximum number of crops per chunk.
     * @param potPerChunk           The maximum number of pots per chunk.
     * @param sprinklerPerChunk     The maximum number of sprinklers per chunk.
     * @param randomTickSpeed       The random tick speed.
     */
    private WorldSetting(
            boolean enableScheduler,
            int minTickUnit,
            int tickCropMode,
            int tickCropInterval,
            int tickPotMode,
            int tickPotInterval,
            int tickSprinklerMode,
            int tickSprinklerInterval,
            boolean offlineTick,
            int maxOfflineTime,
            int maxLoadingTime,
            boolean enableSeason,
            boolean autoSeasonChange,
            int seasonDuration,
            int cropPerChunk,
            int potPerChunk,
            int sprinklerPerChunk,
            int randomTickSpeed
    ) {
        this.enableScheduler = enableScheduler;
        this.minTickUnit = minTickUnit;
        this.tickCropInterval = tickCropInterval;
        this.tickPotInterval = tickPotInterval;
        this.tickSprinklerInterval = tickSprinklerInterval;
        this.offlineTick = offlineTick;
        this.maxOfflineTime = maxOfflineTime;
        this.maxLoadingTime = maxLoadingTime;
        this.enableSeason = enableSeason;
        this.autoSeasonChange = autoSeasonChange;
        this.seasonDuration = seasonDuration;
        this.cropPerChunk = cropPerChunk;
        this.potPerChunk = potPerChunk;
        this.sprinklerPerChunk = sprinklerPerChunk;
        this.randomTickSpeed = randomTickSpeed;
        this.tickCropMode = tickCropMode;
        this.tickPotMode = tickPotMode;
        this.tickSprinklerMode = tickSprinklerMode;
    }

    /**
     * Factory method to create a new instance of WorldSetting.
     *
     * @param enableScheduler       Whether the scheduler is enabled.
     * @param minTickUnit           The minimum unit of tick.
     * @param tickCropMode          The tick mode of crop
     * @param tickCropInterval      The interval for ticking crops.
     * @param tickPotMode           The tick mode of pot
     * @param tickPotInterval       The interval for ticking pots.
     * @param tickSprinklerMode     The tick mode of sprinkler
     * @param tickSprinklerInterval The interval for ticking sprinklers.
     * @param offlineGrow           Whether offline ticking is enabled.
     * @param maxOfflineTime        The maximum offline time allowed.
     * @param enableSeason          Whether seasons are enabled.
     * @param autoSeasonChange      Whether season change is automatic.
     * @param seasonDuration        The duration of each season.
     * @param cropPerChunk          The maximum number of crops per chunk.
     * @param potPerChunk           The maximum number of pots per chunk.
     * @param sprinklerPerChunk     The maximum number of sprinklers per chunk.
     * @param randomTickSpeed       The random tick speed.
     * @return A new WorldSetting instance.
     */
    public static WorldSetting of(
            boolean enableScheduler,
            int minTickUnit,
            int tickCropMode,
            int tickCropInterval,
            int tickPotMode,
            int tickPotInterval,
            int tickSprinklerMode,
            int tickSprinklerInterval,
            boolean offlineGrow,
            int maxOfflineTime,
            int maxLoadingTime,
            boolean enableSeason,
            boolean autoSeasonChange,
            int seasonDuration,
            int cropPerChunk,
            int potPerChunk,
            int sprinklerPerChunk,
            int randomTickSpeed
    ) {
        return new WorldSetting(
                enableScheduler,
                minTickUnit,
                tickCropMode,
                tickCropInterval,
                tickPotMode,
                tickPotInterval,
                tickSprinklerMode,
                tickSprinklerInterval,
                offlineGrow,
                maxOfflineTime,
                maxLoadingTime,
                enableSeason,
                autoSeasonChange,
                seasonDuration,
                cropPerChunk,
                potPerChunk,
                sprinklerPerChunk,
                randomTickSpeed
        );
    }

    /**
     * Checks if the scheduler is enabled.
     *
     * @return true if the scheduler is enabled, false otherwise.
     */
    public boolean enableScheduler() {
        return enableScheduler;
    }

    /**
     * Gets the minimum tick unit.
     *
     * @return The minimum tick unit.
     */
    public int minTickUnit() {
        return minTickUnit;
    }

    /**
     * Gets the interval for ticking crops.
     *
     * @return The tick interval for crops.
     */
    public int tickCropInterval() {
        return tickCropInterval;
    }

    /**
     * Gets the interval for ticking pots.
     *
     * @return The tick interval for pots.
     */
    public int tickPotInterval() {
        return tickPotInterval;
    }

    /**
     * Gets the interval for ticking sprinklers.
     *
     * @return The tick interval for sprinklers.
     */
    public int tickSprinklerInterval() {
        return tickSprinklerInterval;
    }

    /**
     * Checks if offline ticking is enabled.
     *
     * @return true if offline ticking is enabled, false otherwise.
     */
    public boolean offlineTick() {
        return offlineTick;
    }

    /**
     * Gets the max time allowed to load a chunk
     *
     * @return The max loading time
     */
    public int maxLoadingTime() {
        return maxLoadingTime;
    }

    /**
     * Checks if seasons are enabled.
     *
     * @return true if seasons are enabled, false otherwise.
     */
    public boolean enableSeason() {
        return enableSeason;
    }

    /**
     * Checks if automatic season change is enabled.
     *
     * @return true if automatic season change is enabled, false otherwise.
     */
    public boolean autoSeasonChange() {
        return autoSeasonChange;
    }

    /**
     * Gets the duration of each season.
     *
     * @return The duration of each season.
     */
    public int seasonDuration() {
        return seasonDuration;
    }

    /**
     * Gets the maximum number of pots per chunk.
     *
     * @return The maximum number of pots per chunk.
     */
    public int potPerChunk() {
        return potPerChunk;
    }

    /**
     * Gets the maximum number of crops per chunk.
     *
     * @return The maximum number of crops per chunk.
     */
    public int cropPerChunk() {
        return cropPerChunk;
    }

    /**
     * Gets the maximum number of sprinklers per chunk.
     *
     * @return The maximum number of sprinklers per chunk.
     */
    public int sprinklerPerChunk() {
        return sprinklerPerChunk;
    }

    /**
     * Gets the random tick speed.
     *
     * @return The random tick speed.
     */
    public int randomTickSpeed() {
        return randomTickSpeed;
    }

    /**
     * Gets the tick mode of crop
     *
     * @return the mode
     */
    public int tickCropMode() {
        return tickCropMode;
    }

    /**
     * Gets the tick mode of sprinkler
     *
     * @return the mode
     */
    public int tickSprinklerMode() {
        return tickSprinklerMode;
    }

    /**
     * Gets the tick mode of pot
     *
     * @return the mode
     */
    public int tickPotMode() {
        return tickPotMode;
    }

    /**
     * Gets the maximum offline time allowed.
     *
     * @return The maximum offline time.
     */
    public int maxOfflineTime() {
        return maxOfflineTime;
    }

    /**
     * Creates a clone of this WorldSetting instance.
     *
     * @return A cloned instance of WorldSetting.
     */
    @Override
    public WorldSetting clone() {
        try {
            return (WorldSetting) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
