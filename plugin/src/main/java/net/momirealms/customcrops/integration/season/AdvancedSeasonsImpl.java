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

package net.momirealms.customcrops.integration.season;

import me.casperge.realisticseasons.api.SeasonsAPI;
import net.advancedplugins.seasons.api.AdvancedSeasonsAPI;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.helper.Log;
import net.momirealms.customcrops.integration.SeasonInterface;
import org.bukkit.Bukkit;

public class AdvancedSeasonsImpl implements SeasonInterface {

    private final AdvancedSeasonsAPI api;

    public AdvancedSeasonsImpl() {
        this.api = new AdvancedSeasonsAPI();
    }

    @Override
    public CCSeason getSeason(String world) {
        return switch (api.getSeason(Bukkit.getWorld(world))) {
            case "SPRING" -> CCSeason.SPRING;
            case "WINTER" -> CCSeason.WINTER;
            case "SUMMER" -> CCSeason.SUMMER;
            case "FALL" -> CCSeason.AUTUMN;
            default -> CCSeason.UNKNOWN;
        };
    }

    @Override
    public int getDate(String world) {
        return SeasonsAPI.getInstance().getDate(Bukkit.getWorld(world)).getDay();
    }
}
