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
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.integration.SeasonInterface;
import org.bukkit.Bukkit;

public class RealisticSeasonsImpl implements SeasonInterface {

    @Override
    public CCSeason getSeason(String world) {
        return switch (SeasonsAPI.getInstance().getSeason(Bukkit.getWorld(world))) {
            case WINTER -> CCSeason.WINTER;
            case SPRING -> CCSeason.SPRING;
            case SUMMER -> CCSeason.SUMMER;
            case FALL -> CCSeason.AUTUMN;
            case DISABLED, RESTORE -> CCSeason.UNKNOWN;
        };
    }

    @Override
    public int getDate(String world) {
        return SeasonsAPI.getInstance().getDate(Bukkit.getWorld(world)).getDay();
    }
}
