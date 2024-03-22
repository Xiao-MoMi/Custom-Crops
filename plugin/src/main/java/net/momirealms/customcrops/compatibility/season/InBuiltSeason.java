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

import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldInfoData;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class InBuiltSeason implements SeasonInterface {

    private final WorldManager worldManager;

    public InBuiltSeason(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public @Nullable Season getSeason(World world) {
        return worldManager
                .getCustomCropsWorld(world)
                .map(CustomCropsWorld::getSeason)
                .orElse(null);
    }

    @Override
    public int getDate(World world) {
        if (ConfigManager.syncSeasons())
            world = ConfigManager.referenceWorld();
        if (world == null)
            return 0;
        return worldManager
                .getCustomCropsWorld(world)
                .map(CustomCropsWorld::getDate)
                .orElse(0);
    }

    @Override
    public void setSeason(World world, Season season) {
        worldManager.getCustomCropsWorld(world)
                .ifPresent(customWorld -> {
                    WorldInfoData infoData = customWorld.getInfoData();
                    infoData.setSeason(season);
                });
    }

    @Override
    public void setDate(World world, int date) {
        worldManager.getCustomCropsWorld(world)
                .ifPresent(customWorld -> {
                    WorldInfoData infoData = customWorld.getInfoData();
                    infoData.setDate(date);
                });
    }
}