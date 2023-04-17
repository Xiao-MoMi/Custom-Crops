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

package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.object.world.WorldDataManager;

public class WrongSeason implements Condition {

    private final CCSeason[] seasons;

    public WrongSeason(CCSeason[] seasons) {
        this.seasons = seasons;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        String world = simpleLocation.getWorldName();
        CCSeason current = CustomCrops.getInstance().getIntegrationManager().getSeasonInterface().getSeason(world);
        for (CCSeason bad : seasons) {
            if (current == bad) {
                WorldDataManager worldDataManager = CustomCrops.getInstance().getWorldDataManager();
                if (ConfigManager.enableGreenhouse) {
                    for (int i = 0; i < ConfigManager.greenhouseRange; i++) {
                        if (worldDataManager.isGreenhouse(simpleLocation.add(0, i, 0))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}