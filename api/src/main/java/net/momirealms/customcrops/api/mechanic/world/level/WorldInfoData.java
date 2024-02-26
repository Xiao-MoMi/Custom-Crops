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
