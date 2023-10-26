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

package net.momirealms.customcrops.api;

import net.momirealms.customcrops.api.object.CCGrowingCrop;
import net.momirealms.customcrops.api.object.CCPot;
import net.momirealms.customcrops.api.object.CCSprinkler;
import net.momirealms.customcrops.api.object.CCWorldSeason;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public interface CustomCropsAPI {

    /**
     * Get the pot instance at the specified location
     * In order to reduce the memory usage, pot data would be removed
     * if it has no water and no fertilizer
     * But if "only-work-in-loaded-chunks" is true, pot data would not be removed
     * @param location location
     * @return pot
     */
    @Nullable
    CCPot getPotAt(Location location);

    /**
     * Get the on growing crop at the specified location
     * It would be null if the crop already comes to its final stage
     * @param location location
     * @return on growing crop
     */
    @Nullable
    CCGrowingCrop getCropAt(Location location);

    /**
     * If the block is a greenhouse glass in data
     * It would return false if your greenhouse glass lost due to server crash
     * @param location location
     * @return whether the block is greenhouse glass
     */
    boolean isGreenhouseGlass(Location location);

    /**
     * If the chunk has a scarecrow
     * @param location location
     * @return has scarecrow or not
     */
    boolean hasScarecrowInChunk(Location location);

    /**
     * Get the sprinkler at the specified location
     * It would be null if the sprinkler run out of water
     * @param location location
     * @return sprinkler
     */
    @Nullable
    CCSprinkler getSprinklerAt(Location location);

    /**
     * Set the world's season
     * @param world world
     * @param season season
     */
    void setSeason(String world, String season);

    /**
     * Set the world's date
     * @param world world
     * @param date date
     */
    void setDate(String world, int date);

    /**
     * Add a world's date
     * @param world world
     */
    void addDate(String world);

    /**
     * Get a world's season
     * @param world world
     * @return season
     */
    @Nullable
    CCWorldSeason getSeason(String world);

    /**
     * Force the crops to grow in specified seconds
     * @param world world
     * @param seconds time
     */
    void grow(World world, int seconds);

    /**
     * Force the sprinkler to work in specified seconds
     * @param world world
     * @param seconds time
     */
    void sprinklerWork(World world, int seconds);

    /**
     * Force the pots to reduce water and consume fertilizer in specified seconds
     * @param world world
     * @param seconds time
     */
    void consume(World world, int seconds);

    /**
     * Get the api instance
     * It would be null if the plugin is not enabled
     * @return api
     */
    @Nullable
    static CustomCropsAPI getInstance() {
        return CustomCropsPlugin.getInstance().getAPI();
    }
}
