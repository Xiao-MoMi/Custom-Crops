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

package net.momirealms.customcrops.bukkit.integration.season;

import net.advancedplugins.seasons.Core;
import net.momirealms.customcrops.api.core.world.Season;
import net.momirealms.customcrops.api.integration.SeasonProvider;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class AdvancedSeasonsProvider implements SeasonProvider {

    @NotNull
    @Override
    public Season getSeason(@NotNull World world) {
        net.advancedplugins.seasons.enums.Season season = Core.getSeasonHandler().getSeason(world);
        if (season == null) {
            return Season.DISABLE;
        }
        return switch (season.getType()) {
            case SPRING -> Season.SPRING;
            case WINTER -> Season.WINTER;
            case SUMMER -> Season.SUMMER;
            case FALL -> Season.AUTUMN;
        };
    }

    @Override
    public String identifier() {
        return "AdvancedSeasons";
    }
}
