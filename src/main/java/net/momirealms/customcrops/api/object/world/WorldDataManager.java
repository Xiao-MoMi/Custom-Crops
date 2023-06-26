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

package net.momirealms.customcrops.api.object.world;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.OfflineReplaceTask;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import net.momirealms.customcrops.api.object.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class WorldDataManager extends Function {

    private final ConcurrentHashMap<String, CCWorld> worldMap;
    private final CustomCrops plugin;
    private final WorldListener worldListener;
    private SlimeWorldListener slimeWorldListener;

    public WorldDataManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.worldMap = new ConcurrentHashMap<>();
        this.worldListener = new WorldListener(this);
        try {
            Class.forName("com.infernalsuite.aswm.api.world.SlimeWorld");
            this.slimeWorldListener = new SlimeWorldListener(this);
        } catch (ClassNotFoundException ignored) {
        }
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(worldListener, plugin);
        if (slimeWorldListener != null) Bukkit.getPluginManager().registerEvents(slimeWorldListener, plugin);
        for (CCWorld ccWorld : worldMap.values()) {
            ccWorld.load();
        }
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(worldListener);
        if (slimeWorldListener != null) HandlerList.unregisterAll(slimeWorldListener);
        for (CCWorld ccWorld : worldMap.values()) {
            ccWorld.unload();
        }
    }

    @Override
    public void disable() {
        this.unload();
        for (CCWorld ccWorld : worldMap.values()) {
            ccWorld.disable();
        }
        this.worldMap.clear();
    }

    public void loadWorld(World world) {
        if (ConfigManager.debugWorld) AdventureUtils.consoleMessage("World " + world.getName() + " is trying to load");
        if (!isWorldAllowed(world)) return;
        CCWorld ccWorld = new CCWorld(world, plugin);
        ccWorld.init();
        ccWorld.load();
        ccWorld.onReachPoint();
        worldMap.put(world.getName(), ccWorld);
        if (ConfigManager.debugWorld) AdventureUtils.consoleMessage("World " + world.getName() + " is loaded");
    }

    public void unloadWorld(World world) {
        CCWorld ccWorld = worldMap.remove(world.getName());
        if (ccWorld != null) {
            ccWorld.disable();
            if (ConfigManager.debugWorld) AdventureUtils.consoleMessage("World " + world.getName() + " is unloaded");
        }
    }

    public void removeCropData(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.removeCropData(simpleLocation);
        }
    }

    public void addCropData(SimpleLocation simpleLocation, GrowingCrop growingCrop, boolean grow) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addCropData(simpleLocation, growingCrop, grow);
        }
    }

    public int getChunkCropAmount(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.getChunkCropAmount(simpleLocation);
        }
        return -1;
    }

    public void removeGreenhouse(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.removeGreenhouse(simpleLocation);
        }
    }

    public void addGreenhouse(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addGreenhouse(simpleLocation);
        }
    }

    public boolean isGreenhouse(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.isGreenhouse(simpleLocation);
        }
        return false;
    }

    public boolean isWorldAllowed(World world) {
        return ConfigManager.whiteListWorlds == ConfigManager.worldList.contains(world.getName());
    }

    public void removePotData(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.removePotData(simpleLocation);
        }
    }

    public void removeSprinklerData(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.removeSprinklerData(simpleLocation);
        }
    }

    @Nullable
    public Sprinkler getSprinklerData(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.getSprinklerData(simpleLocation);
        }
        return null;
    }

    public void addSprinklerData(SimpleLocation simpleLocation, Sprinkler sprinkler) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addSprinklerData(simpleLocation, sprinkler);
        }
    }

    public void addScarecrow(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addScarecrow(simpleLocation);
        }
    }

    public void removeScarecrow(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.removeScarecrow(simpleLocation);
        }
    }

    public boolean hasScarecrow(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.hasScarecrow(simpleLocation);
        }
        return false;
    }

    public void addWaterToPot(SimpleLocation simpleLocation, int amount, String pot_id) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addWaterToPot(simpleLocation, amount, pot_id);
        }
    }

    public void addFertilizerToPot(SimpleLocation simpleLocation, Fertilizer fertilizer, String pot_id) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addFertilizerToPot(simpleLocation, fertilizer, pot_id);
        }
    }

    @Nullable
    public Pot getPotData(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.getPotData(simpleLocation);
        }
        return null;
    }

    public void addPotData(SimpleLocation simpleLocation, Pot pot) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addPotData(simpleLocation, pot);
        }
    }

    public void addOfflineTask(SimpleLocation simpleLocation, OfflineReplaceTask offlineReplaceTask) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            ccWorld.addOfflineReplaceTask(simpleLocation, offlineReplaceTask);
        }
    }

    public void addWaterToSprinkler(SimpleLocation simpleLocation, int add, SprinklerConfig sprinklerConfig) {
        Sprinkler sprinkler = getSprinklerData(simpleLocation);
        if (sprinkler != null) {
            sprinkler.setWater(Math.min(add + sprinkler.getWater(), sprinklerConfig.getStorage()));
        } else {
            Sprinkler newSprinkler = new Sprinkler(sprinklerConfig.getKey(), Math.min(add, sprinklerConfig.getStorage()));
            addSprinklerData(simpleLocation, newSprinkler);
        }
    }

    public boolean addCropPointAt(SimpleLocation simpleLocation, int points) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.addCropPointAt(simpleLocation, points);
        }
        return false;
    }

    @Nullable
    public GrowingCrop getCropData(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.getCropData(simpleLocation);
        }
        return null;
    }

    @Nullable
    public CCWorld getWorld(String world) {
        return worldMap.get(world);
    }

    public void loadChunk(Chunk chunk, World world) {
        CCWorld ccWorld = worldMap.get(world.getName());
        if (ccWorld != null) {
            ccWorld.loadChunk(new ChunkCoordinate(chunk.getX(), chunk.getZ()));
        }
    }

    public void unloadChunk(Chunk chunk, World world) {
        CCWorld ccWorld = worldMap.get(world.getName());
        if (ccWorld != null) {
            ccWorld.unloadChunk(new ChunkCoordinate(chunk.getX(), chunk.getZ()));
        }
    }

    @Nullable
    public String getCorruptedPotOriginalKey(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.getCorruptedPotOriginalKey(simpleLocation);
        }
        return null;
    }

    public String removeCorrupted(SimpleLocation simpleLocation) {
        CCWorld ccWorld = worldMap.get(simpleLocation.getWorldName());
        if (ccWorld != null) {
            return ccWorld.removeCorrupted(simpleLocation);
        }
        return null;
    }

    public void fixCorruptedData(String world) {
        CCWorld ccWorld = worldMap.get(world);
        if (ccWorld != null) {
            ccWorld.fixCorruptedData();
        }
    }
}
