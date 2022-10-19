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

package net.momirealms.customcrops.api.utils;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.config.MessageConfig;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class SeasonUtils {

    /**
     * Set season for a specified world
     * @param world world
     * @param season season
     */
    public static void setSeason(World world, CCSeason season) {
        CustomCrops.plugin.getCropManager().getSeasonAPI().setSeason(season, world);
    }

    /**
     * return a world's season, if it has no season, it would return a new season
     * @param world world
     * @return season
     */
    @NotNull
    public static CCSeason getSeason(World world) {
        return CustomCrops.plugin.getCropManager().getSeasonAPI().getSeason(world);
    }

    /**
     * remove a world's season data from cache
     * @param world world
     */
    public static void unloadSeason(World world) {
        CustomCrops.plugin.getCropManager().getSeasonAPI().unloadWorld(world);
    }

    public static String getSeasonText(CCSeason season) {
        return switch (season) {
            case SPRING -> MessageConfig.spring;
            case SUMMER -> MessageConfig.summer;
            case AUTUMN -> MessageConfig.autumn;
            case WINTER -> MessageConfig.winter;
            default -> throw new IllegalStateException("Unexpected value: " + season);
        };
    }
}
