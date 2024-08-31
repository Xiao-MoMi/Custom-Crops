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

package net.momirealms.customcrops.api.core.world;

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
    private final boolean tickCropRandomly;
    private final boolean tickPotRandomly;
    private final boolean tickSprinklerRandomly;

    private WorldSetting(
            boolean enableScheduler,
            int minTickUnit,
            boolean tickCropRandomly,
            int tickCropInterval,
            boolean tickPotRandomly,
            int tickPotInterval,
            boolean tickSprinklerRandomly,
            int tickSprinklerInterval,
            boolean offlineTick,
            int maxOfflineTime,
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
        this.enableSeason = enableSeason;
        this.autoSeasonChange = autoSeasonChange;
        this.seasonDuration = seasonDuration;
        this.cropPerChunk = cropPerChunk;
        this.potPerChunk = potPerChunk;
        this.sprinklerPerChunk = sprinklerPerChunk;
        this.randomTickSpeed = randomTickSpeed;
        this.tickCropRandomly = tickCropRandomly;
        this.tickPotRandomly = tickPotRandomly;
        this.tickSprinklerRandomly = tickSprinklerRandomly;
    }

    public static WorldSetting of(
            boolean enableScheduler,
            int minTickUnit,
            boolean tickCropRandomly,
            int tickCropInterval,
            boolean tickPotRandomly,
            int tickPotInterval,
            boolean tickSprinklerRandomly,
            int tickSprinklerInterval,
            boolean offlineGrow,
            int maxOfflineTime,
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
                tickCropRandomly,
                tickCropInterval,
                tickPotRandomly,
                tickPotInterval,
                tickSprinklerRandomly,
                tickSprinklerInterval,
                offlineGrow,
                maxOfflineTime,
                enableSeason,
                autoSeasonChange,
                seasonDuration,
                cropPerChunk,
                potPerChunk,
                sprinklerPerChunk,
                randomTickSpeed
        );
    }

    public boolean enableScheduler() {
        return enableScheduler;
    }

    public int minTickUnit() {
        return minTickUnit;
    }

    public int tickCropInterval() {
        return tickCropInterval;
    }

    public int tickPotInterval() {
        return tickPotInterval;
    }

    public int tickSprinklerInterval() {
        return tickSprinklerInterval;
    }

    public boolean offlineTick() {
        return offlineTick;
    }

    public boolean enableSeason() {
        return enableSeason;
    }

    public boolean autoSeasonChange() {
        return autoSeasonChange;
    }

    public int seasonDuration() {
        return seasonDuration;
    }

    public int potPerChunk() {
        return potPerChunk;
    }

    public int cropPerChunk() {
        return cropPerChunk;
    }

    public int sprinklerPerChunk() {
        return sprinklerPerChunk;
    }

    public int randomTickSpeed() {
        return randomTickSpeed;
    }

    public boolean randomTickCrop() {
        return tickCropRandomly;
    }

    public boolean randomTickSprinkler() {
        return tickSprinklerRandomly;
    }

    public boolean randomTickPot() {
        return tickPotRandomly;
    }

    public int maxOfflineTime() {
        return maxOfflineTime;
    }

    @Override
    public WorldSetting clone() {
        try {
            return (WorldSetting) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
