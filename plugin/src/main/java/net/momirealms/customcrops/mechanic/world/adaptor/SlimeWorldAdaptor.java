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

package net.momirealms.customcrops.mechanic.world.adaptor;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.events.LoadSlimeWorldEvent;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class SlimeWorldAdaptor extends AbstractWorldAdaptor {

    private final SlimePlugin slimePlugin;

    public SlimeWorldAdaptor(WorldManager worldManager) {
        super(worldManager);
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }

    @EventHandler (ignoreCancelled = true)
    public void onSlimeWorldLoad(LoadSlimeWorldEvent event) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        if (worldManager.isMechanicEnabled(event.getWorld())) {
            worldManager.loadWorld(event.getWorld());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
    }

    @Override
    public void unload(CustomCropsWorld customCropsWorld) {

    }

    @Override
    public void init(CustomCropsWorld customCropsWorld) {

    }

    @Override
    public void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {

    }

    @Override
    public void unloadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {

    }

    @Override
    public void saveDynamicData(CustomCropsWorld ccWorld, CustomCropsChunk chunk) {

    }

    private boolean isSlimeWorld(String name) {
        return slimePlugin.getWorld(name) != null;
    }
}
