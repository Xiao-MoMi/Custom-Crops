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
    private final int pointInterval;
    private final int tickPotInterval;
    private final int tickSprinklerInterval;
    private final boolean offlineGrow;
    private final boolean enableSeason;
    private final boolean autoSeasonChange;
    private final int seasonDuration;
    private final int cropPerChunk;
    private final int potPerChunk;
    private final int sprinklerPerChunk;

    private WorldSetting(
            boolean enableScheduler,
            int pointInterval,
            int tickPotInterval,
            int tickSprinklerInterval,
            boolean offlineGrow,
            boolean enableSeason,
            boolean autoSeasonChange,
            int seasonDuration,
            int cropPerChunk,
            int potPerChunk,
            int sprinklerPerChunk
    ) {
        this.enableScheduler = enableScheduler;
        this.pointInterval = pointInterval;
        this.tickPotInterval = tickPotInterval;
        this.tickSprinklerInterval = tickSprinklerInterval;
        this.offlineGrow = offlineGrow;
        this.enableSeason = enableSeason;
        this.autoSeasonChange = autoSeasonChange;
        this.seasonDuration = seasonDuration;
        this.cropPerChunk = cropPerChunk;
        this.potPerChunk = potPerChunk;
        this.sprinklerPerChunk = sprinklerPerChunk;
    }

    public static WorldSetting of(
            boolean enableScheduler,
            int pointInterval,
            int tickPotInterval,
            int tickSprinklerInterval,
            boolean offlineGrow,
            boolean enableSeason,
            boolean autoSeasonChange,
            int seasonDuration,
            int cropPerChunk,
            int potPerChunk,
            int sprinklerPerChunk
    ) {
        return new WorldSetting(
                enableScheduler,
                pointInterval,
                tickPotInterval,
                tickSprinklerInterval,
                offlineGrow,
                enableSeason,
                autoSeasonChange,
                seasonDuration,
                cropPerChunk,
                potPerChunk,
                sprinklerPerChunk
        );
    }

    public boolean isEnableScheduler() {
        return enableScheduler;
    }

    public int getPointInterval() {
        return pointInterval;
    }

    public int getTickPotInterval() {
        return tickPotInterval;
    }

    public int getTickSprinklerInterval() {
        return tickSprinklerInterval;
    }

    public boolean isOfflineGrow() {
        return offlineGrow;
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

    @Override
    public WorldSetting clone() {
        try {
            return (WorldSetting) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
