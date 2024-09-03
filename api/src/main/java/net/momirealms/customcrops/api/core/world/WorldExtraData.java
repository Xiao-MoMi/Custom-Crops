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

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * A class representing additional data associated with a world, such as the current season,
 * date, and any extra arbitrary data stored as key-value pairs. This class provides methods
 * for managing the season and date of the world, as well as adding, removing, and retrieving
 * extra data.
 */
public class WorldExtraData {

    @SerializedName("season")
    private Season season;
    @SerializedName("date")
    private int date;
    @SerializedName("extra")
    private HashMap<String, Object> extra;

    /**
     * Constructs a new WorldExtraData instance with the specified season and date.
     * Initializes an empty HashMap for extra data.
     *
     * @param season The initial season of the world.
     * @param date   The initial date of the world.
     */
    public WorldExtraData(Season season, int date) {
        this.season = season;
        this.date = date;
        this.extra = new HashMap<>();
    }

    /**
     * Creates an empty WorldExtraData instance with default values.
     *
     * @return A new WorldExtraData instance with the season set to SPRING and date set to 1.
     */
    public static WorldExtraData empty() {
        return new WorldExtraData(Season.SPRING, 1);
    }

    /**
     * Adds extra data to the world data storage.
     *
     * @param key   The key under which the data will be stored.
     * @param value The value to store.
     */
    public void addExtraData(String key, Object value) {
        this.extra.put(key, value);
    }

    /**
     * Removes extra data from the world data storage.
     *
     * @param key The key of the data to remove.
     */
    public void removeExtraData(String key) {
        this.extra.remove(key);
    }

    /**
     * Retrieves extra data from the world data storage.
     *
     * @param key The key of the data to retrieve.
     * @return The data associated with the key, or null if the key does not exist.
     */
    @Nullable
    public Object getExtraData(String key) {
        return this.extra.get(key);
    }

    /**
     * Gets the current season of the world.
     *
     * @return The current season.
     */
    public Season getSeason() {
        if (season == null) season = Season.SPRING;
        return season;
    }

    /**
     * Sets the season of the world.
     *
     * @param season The new season to set.
     */
    public void setSeason(Season season) {
        this.season = season;
    }

    /**
     * Gets the current date of the world.
     *
     * @return The current date.
     */
    public int getDate() {
        return date;
    }

    /**
     * Sets the date of the world.
     *
     * @param date The new date to set.
     */
    public void setDate(int date) {
        this.date = date;
    }

    /**
     * Returns a string representation of the WorldExtraData.
     *
     * @return A string containing the season and date information.
     */
    @Override
    public String toString() {
        return "WorldExtraData{" +
                "season=" + season +
                ", date=" + date +
                ", extra=" + extra +
                '}';
    }
}
