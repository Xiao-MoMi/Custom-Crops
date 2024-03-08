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
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class RealisticSeasonsImpl implements SeasonInterface {

    @Override
    public @Nullable Season getSeason(World world) {
        return switch (SeasonsAPI.getInstance().getSeason(world)) {
            case WINTER -> Season.WINTER;
            case SPRING -> Season.SPRING;
            case SUMMER -> Season.SUMMER;
            case FALL -> Season.AUTUMN;
            case DISABLED, RESTORE -> null;
        };
    }

    @Override
    public int getDate(World world) {
        return SeasonsAPI.getInstance().getDate(world).getDay();
    }
}
