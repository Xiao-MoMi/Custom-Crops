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

import net.advancedplugins.seasons.api.AdvancedSeasonsAPI;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;

public class AdvancedSeasonsImpl implements SeasonInterface {

    private final AdvancedSeasonsAPI api;

    public AdvancedSeasonsImpl() {
        this.api = new AdvancedSeasonsAPI();
    }

    @Override
    public Season getSeason(World world) {
        return switch (api.getSeason(world)) {
            case "SPRING" -> Season.SPRING;
            case "WINTER" -> Season.WINTER;
            case "SUMMER" -> Season.SUMMER;
            case "FALL" -> Season.AUTUMN;
            default -> null;
        };
    }

    @Override
    public int getDate(World world) {
        return 0;
    }
}
