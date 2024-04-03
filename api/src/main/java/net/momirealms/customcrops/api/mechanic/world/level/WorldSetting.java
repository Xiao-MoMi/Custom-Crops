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

package net.momirealms.customcrops.api.mechanic.world.level;

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
    private final boolean scheduledTick;

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
        this.scheduledTick = !(tickCropRandomly && tickPotRandomly && tickSprinklerRandomly);
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

    public boolean isSchedulerEnabled() {
        return enableScheduler;
    }

    public int getMinTickUnit() {
        return minTickUnit;
    }

    public int getTickCropInterval() {
        return tickCropInterval;
    }

    public int getTickPotInterval() {
        return tickPotInterval;
    }

    public int getTickSprinklerInterval() {
        return tickSprinklerInterval;
    }

    public boolean isOfflineTick() {
        return offlineTick;
    }

    public boolean isEnableSeason() {
        return enableSeason;
    }

    public boolean isAutoSeasonChange() {
        return autoSeasonChange;
    }

    public int getSeasonDuration() {
        return seasonDuration;
    }

    public int getPotPerChunk() {
        return potPerChunk;
    }

    public int getCropPerChunk() {
        return cropPerChunk;
    }

    public int getSprinklerPerChunk() {
        return sprinklerPerChunk;
    }

    public int getRandomTickSpeed() {
        return randomTickSpeed;
    }

    public boolean isScheduledTick() {
        return scheduledTick;
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

    public int getMaxOfflineTime() {
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
