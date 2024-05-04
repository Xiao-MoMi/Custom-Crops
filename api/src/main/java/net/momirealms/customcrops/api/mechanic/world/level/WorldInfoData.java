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

    public WorldInfoData(Season season, int date) {
        this.season = season;
        this.date = date;
    }

    public static WorldInfoData empty() {
        return new WorldInfoData(Season.SPRING, 1);
    }

    /**
     * Get season
     *
     * @return season
     */
    public Season getSeason() {
        if (season == null) season = Season.SPRING;
        return season;
    }

    /**
     * Set season
     *
     * @param season the new season
     */
    public void setSeason(Season season) {
        this.season = season;
    }

    /**
     * Get date
     *
     * @return date
     */
    public int getDate() {
        return date;
    }

    /**
     * Set date
     *
     * @param date the new date
     */
    public void setDate(int date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WorldInfoData{" +
                "season=" + season +
                ", date=" + date +
                '}';
    }
}
