package net.momirealms.customcrops.api.object.season;

import net.momirealms.customcrops.api.object.basic.ConfigManager;

public class SeasonData {

    private CCSeason ccSeason;
    private int date;
    private final String world;

    public SeasonData(String world, CCSeason ccSeason, int date) {
        this.world = world;
        this.ccSeason = ccSeason;
        this.date = date;
    }

    public SeasonData(String world) {
        this.world = world;
        this.ccSeason = CCSeason.SPRING;
        this.date = 1;
    }

    public CCSeason getSeason() {
        return ccSeason;
    }

    public int getDate() {
        return date;
    }

    public void addDate() {
        this.date++;
        if (date > ConfigManager.seasonInterval) {
            this.date = 1;
            this.ccSeason = getNextSeason(ccSeason);
        }
    }

    public CCSeason getNextSeason(CCSeason ccSeason) {
        return switch (ccSeason) {
            case AUTUMN -> CCSeason.WINTER;
            case WINTER -> CCSeason.SPRING;
            case SPRING -> CCSeason.SUMMER;
            case SUMMER -> CCSeason.AUTUMN;
            default -> CCSeason.UNKNOWN;
        };
    }

    public void changeSeason(CCSeason ccSeason) {
        this.ccSeason = ccSeason;
    }

    public String getWorld() {
        return world;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
