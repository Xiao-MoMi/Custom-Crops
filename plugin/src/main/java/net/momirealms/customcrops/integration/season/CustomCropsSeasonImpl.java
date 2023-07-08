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

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.integration.SeasonInterface;

public class CustomCropsSeasonImpl implements SeasonInterface {

    @Override
    public CCSeason getSeason(String world) {
        return CustomCrops.getInstance().getSeasonManager().getSeason(world);
    }

    @Override
    public int getDate(String world) {
        return CustomCrops.getInstance().getSeasonManager().getDate(world);
    }
}