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

package net.momirealms.customcrops.integrations.season;

import me.casperge.realisticseasons.api.SeasonsAPI;
import net.momirealms.customcrops.Function;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RealisticSeasonsHook extends Function implements SeasonInterface {

    private SeasonsAPI api;

    @Override
    public void load() {
        super.load();
        this.api = SeasonsAPI.getInstance();
    }

    @Override
    public void unload() {
        super.unload();
    }

    @Override
    public boolean isWrongSeason(World world, @Nullable CCSeason[] seasonList) {
        if (seasonList == null) return false;
        for (CCSeason season : seasonList) {
            if (season == getSeason(world)) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void unloadWorld(World world) {
    }

    /**
     * Get the season from RealisticSeasons
     * @param world world
     * @return CustomCrops Season
     */
    @Override
    @NotNull
    public CCSeason getSeason(World world){
        switch (api.getSeason(world)){
            case SPRING -> {return CCSeason.SPRING;}
            case SUMMER -> {return CCSeason.SUMMER;}
            case WINTER -> {return CCSeason.WINTER;}
            case FALL -> {return CCSeason.AUTUMN;}
        }
        return CCSeason.UNKNOWN;
    }

    /**
     * Set season for RealisticSeasons
     * @param season season
     * @param world world
     */
    @Override
    public void setSeason(CCSeason season, World world) {
        me.casperge.realisticseasons.season.Season rsSeason = switch (season) {
            case SPRING -> me.casperge.realisticseasons.season.Season.SPRING;
            case SUMMER -> me.casperge.realisticseasons.season.Season.SUMMER;
            case AUTUMN -> me.casperge.realisticseasons.season.Season.FALL;
            case WINTER -> me.casperge.realisticseasons.season.Season.WINTER;
            default -> throw new IllegalStateException("Unexpected value: " + season);
        };
        api.setSeason(world, rsSeason);
    }
}