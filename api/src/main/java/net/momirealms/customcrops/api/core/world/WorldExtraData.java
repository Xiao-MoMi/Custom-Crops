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

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class WorldExtraData {

    @SerializedName("season")
    private Season season;
    @SerializedName("date")
    private int date;
    @SerializedName("extra")
    private HashMap<String, Object> extra;

    public WorldExtraData(Season season, int date) {
        this.season = season;
        this.date = date;
        this.extra = new HashMap<>();
    }

    public static WorldExtraData empty() {
        return new WorldExtraData(Season.SPRING, 1);
    }

    public void addExtraData(String key, Object value) {
        this.extra.put(key, value);
    }

    public void removeExtraData(String key) {
        this.extra.remove(key);
    }

    @Nullable
    public Object getExtraData(String key) {
        return this.extra.get(key);
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
        return "WorldExtraData{" +
                "season=" + season +
                ", date=" + date +
                '}';
    }
}
