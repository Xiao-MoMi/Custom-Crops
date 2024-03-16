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

package net.momirealms.customcrops.compatibility.season;

import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.calendar.Date;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class RealisticSeasonsImpl implements SeasonInterface {

    private final SeasonsAPI api;

    public RealisticSeasonsImpl() {
        this.api = SeasonsAPI.getInstance();
    }

    @Override
    public @Nullable Season getSeason(World world) {
        return switch (api.getSeason(world)) {
            case WINTER -> Season.WINTER;
            case SPRING -> Season.SPRING;
            case SUMMER -> Season.SUMMER;
            case FALL -> Season.AUTUMN;
            case DISABLED, RESTORE -> null;
        };
    }

    @Override
    public int getDate(World world) {
        return api.getDate(world).getDay();
    }

    @Override
    public void setSeason(World world, Season season) {
        me.casperge.realisticseasons.season.Season rsSeason = switch (season) {
            case AUTUMN -> me.casperge.realisticseasons.season.Season.FALL;
            case SUMMER -> me.casperge.realisticseasons.season.Season.SUMMER;
            case WINTER -> me.casperge.realisticseasons.season.Season.WINTER;
            case SPRING -> me.casperge.realisticseasons.season.Season.SPRING;
        };
        api.setSeason(world, rsSeason);
    }

    @Override
    public void setDate(World world, int date) {
        Date rsDate = api.getDate(world);
        api.setDate(world, new Date(date, rsDate.getMonth(), rsDate.getYear()));
    }
}
