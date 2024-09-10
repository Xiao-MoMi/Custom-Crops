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

import me.casperge.realisticseasons.api.SeasonsAPI;
import net.momirealms.customcrops.api.core.world.Season;
import net.momirealms.customcrops.api.integration.SeasonProvider;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class RealisticSeasonsProvider implements SeasonProvider {

    @NotNull
    @Override
    public Season getSeason(@NotNull World world) {
        try {
            return switch (SeasonsAPI.getInstance().getSeason(world)) {
                case WINTER -> Season.WINTER;
                case SPRING -> Season.SPRING;
                case SUMMER -> Season.SUMMER;
                case FALL -> Season.AUTUMN;
                case DISABLED, RESTORE -> Season.DISABLE;
            };
        } catch (NullPointerException e) {
            return Season.DISABLE;
        }
    }

    @Override
    public String identifier() {
        return "RealisticSeasons";
    }
}
