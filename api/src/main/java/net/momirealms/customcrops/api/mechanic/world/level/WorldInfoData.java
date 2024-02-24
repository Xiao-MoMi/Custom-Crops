package net.momirealms.customcrops.api.mechanic.world.level;

import com.google.gson.annotations.SerializedName;
import net.momirealms.customcrops.api.mechanic.world.season.Season;

public class WorldInfoData {

    @SerializedName("season")
    private Season season;
    @SerializedName("date")
    private int date;
    @SerializedName("alive")
    private long lastAliveTime;

    public WorldInfoData(Season season, int date, long lastAliveTime) {
        this.season = season;
        this.date = date;
        this.lastAliveTime = lastAliveTime;
    }

    public static WorldInfoData empty() {
        return new WorldInfoData(Season.SPRING, 1, System.currentTimeMillis());
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public long getLastAliveTime() {
        return lastAliveTime;
    }

    public void setLastAliveTime(long lastAliveTime) {
        this.lastAliveTime = lastAliveTime;
    }
}
