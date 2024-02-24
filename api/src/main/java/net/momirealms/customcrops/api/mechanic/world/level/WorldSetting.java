package net.momirealms.customcrops.api.mechanic.world.level;

public class WorldSetting implements Cloneable {

    private boolean enableScheduler;
    private int pointInterval;
    private int tickPotInterval;
    private int tickSprinklerInterval;
    private boolean offlineGrow;
    private boolean enableSeason;
    private boolean autoSeasonChange;
    private int seasonDuration;
    private int cropPerChunk;
    private int potPerChunk;
    private int sprinklerPerChunk;

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
        return potPerChunk < 0 ? Integer.MAX_VALUE : potPerChunk;
    }

    public int getCropPerChunk() {
        return cropPerChunk < 0 ? Integer.MAX_VALUE : cropPerChunk;
    }

    public int getSprinklerPerChunk() {
        return sprinklerPerChunk < 0 ? Integer.MAX_VALUE : sprinklerPerChunk;
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
