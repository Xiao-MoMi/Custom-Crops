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

package net.momirealms.customcrops.managers;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.CustomWorldEvent;
import net.momirealms.customcrops.config.ConfigUtil;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.FertilizerConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WorldState;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.MiscUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomWorld {

    private final World world;
    private final ConcurrentHashMap<SimpleLocation, String> cropCache;
    private final ConcurrentHashMap<SimpleLocation, Sprinkler> sprinklerCache;
    private final ConcurrentHashMap<SimpleLocation, Fertilizer> fertilizerCache;
    private final Set<SimpleLocation> watered;
    private final CropManager cropManager;

    public CustomWorld(World world, CropManager cropManager) {
        this.world = world;
        this.cropCache = new ConcurrentHashMap<>(4096);
        this.fertilizerCache = new ConcurrentHashMap<>(2048);
        this.sprinklerCache = new ConcurrentHashMap<>(1024);
        this.cropManager = cropManager;
        this.watered = Collections.synchronizedSet(new HashSet<>());
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
            loadCropCache();
            loadSprinklerCache();
            loadFertilizerCache();
            Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                CustomWorldEvent customWorldEvent = new CustomWorldEvent(world, WorldState.LOAD);
                Bukkit.getPluginManager().callEvent(customWorldEvent);
            });
        });
    }

    public void unload(boolean disable) {
        if (disable) {
            unloadCrop();
            unloadSprinkler();
            unloadFertilizer();
        }
        else {
            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
                unloadCrop();
                unloadSprinkler();
                unloadFertilizer();
                Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                    CustomWorldEvent customWorldEvent = new CustomWorldEvent(world, WorldState.UNLOAD);
                    Bukkit.getPluginManager().callEvent(customWorldEvent);
                });
            });
        }
    }

    private void loadFertilizerCache() {
        YamlConfiguration data = loadData("fertilizers", world.getName());
        for (String key : data.getKeys(false)) {
            String[] loc = StringUtils.split(key, ",");
            SimpleLocation location = new SimpleLocation(world.getName(), Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            String fertilizer = data.getString(key + ".type");
            int times = data.getInt(key + ".times");
            Fertilizer fertilizerConfig = FertilizerConfig.FERTILIZERS.get(fertilizer);
            if (fertilizerConfig != null) {
                fertilizerCache.put(location, fertilizerConfig.getWithTimes(times));
            }
        }
    }

    private void unloadFertilizer() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<SimpleLocation, Fertilizer> en : fertilizerCache.entrySet()) {
            SimpleLocation location = en.getKey();
            String loc = location.getX() + "," + location.getY() + "," + location.getZ();
            data.set(loc + ".times", en.getValue().getTimes());
            data.set(loc + ".type", en.getValue().getKey());
        }
        try {
            data.save(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "fertilizers.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
            AdventureUtil.consoleMessage("<red>[CustomCrops] Failed to save pot data for world " + world.getName() + "</red>");
        }
    }

    private void loadSprinklerCache() {
        YamlConfiguration data = loadData("sprinklers", world.getName());
        for (String key : data.getKeys(false)) {
            String[] loc = StringUtils.split(key, ",");
            SimpleLocation location = new SimpleLocation(world.getName(), Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            String sprinkler = data.getString(key + ".type");
            int water = data.getInt(key + ".water");
            Sprinkler sprinklerConfig = SprinklerConfig.SPRINKLERS_CONFIG.get(sprinkler + "CONFIG");
            if (sprinklerConfig != null) {
                if (water > sprinklerConfig.getWater()) water = sprinklerConfig.getWater();
                Sprinkler sprinklerInstance = new Sprinkler(sprinklerConfig.getKey(), sprinklerConfig.getRange(), water);
                sprinklerCache.put(location, sprinklerInstance);
            }
        }
    }

    private void unloadSprinkler() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<SimpleLocation, Sprinkler> en : sprinklerCache.entrySet()) {
            SimpleLocation location = en.getKey();
            String loc = location.getX() + "," + location.getY() + "," + location.getZ();
            data.set(loc + ".water", en.getValue().getWater());
            data.set(loc + ".type", en.getValue().getKey());
        }
        try {
            data.save(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "sprinklers.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
            AdventureUtil.consoleMessage("<red>[CustomCrops] Failed to save sprinkler data for world " + world.getName() + "</red>");
        }
    }

    private void loadCropCache() {
        YamlConfiguration data = loadData("crops", world.getName());
        for (String key : data.getKeys(false)) {
            String[] loc = StringUtils.split(key, ",");
            SimpleLocation location = new SimpleLocation(world.getName(), Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            String crop = data.getString(key);
            if (crop == null) return;
            if (CropConfig.CROPS.containsKey(crop)) {
                cropCache.put(location, crop);
            }
        }
    }

    private void unloadCrop() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<SimpleLocation, String> en : cropCache.entrySet()) {
            SimpleLocation location = en.getKey();
            String loc = location.getX() + "," + location.getY() + "," + location.getZ();
            data.set(loc, en.getValue());
        }
        try {
            data.save(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "crops.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
            AdventureUtil.consoleMessage("<red>[CustomCrops] Failed to save crop data for world " + world.getName() + "</red>");
        }
    }

    public void growWire(int time) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        CropModeInterface cropMode = cropManager.getCropMode();
        bukkitScheduler.runTaskAsynchronously(CustomCrops.plugin, () -> {
            route();
            for (SimpleLocation location : cropCache.keySet()) {
                bukkitScheduler.runTaskLaterAsynchronously(CustomCrops.plugin, () -> {
                    Location seedLoc = MiscUtils.getLocation(location);
                    if (seedLoc == null) return;
                    if (cropMode.growJudge(seedLoc)) {
                        cropCache.remove(location);
                    }
                }, new Random().nextInt(time));
            }
        });
    }

    public void growFrame(int time) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        CropModeInterface cropMode = cropManager.getCropMode();
        bukkitScheduler.runTaskAsynchronously(CustomCrops.plugin, () -> {
            route();
            for (SimpleLocation location : cropCache.keySet()) {
                long random = new Random().nextInt(time);
                bukkitScheduler.runTaskLater(CustomCrops.plugin, () -> {
                    Location seedLoc = MiscUtils.getLocation(location);
                    if (seedLoc == null) return;
                    cropMode.loadChunk(seedLoc);
                }, random);
                bukkitScheduler.runTaskLater(CustomCrops.plugin, () -> {
                    Location seedLoc = MiscUtils.getLocation(location);
                    if (seedLoc == null) return;
                    if (cropMode.growJudge(seedLoc)) {
                        cropCache.remove(location);
                    }
                }, random + 5);
            }
        });
    }

    private void route() {
        watered.clear();
        for (Map.Entry<SimpleLocation, Sprinkler> sprinklerEntry : sprinklerCache.entrySet()) {
            sprinklerWork(sprinklerEntry.getKey(), sprinklerEntry.getValue());
        }
        for (Map.Entry<SimpleLocation, Fertilizer> fertilizerEntry : fertilizerCache.entrySet()) {
            Fertilizer fertilizer = fertilizerEntry.getValue();
            if (fertilizer.getTimes() > 1) {
                fertilizer.setTimes(fertilizer.getTimes() - 1);
            }
            else {
                fertilizerCache.remove(fertilizerEntry.getKey());
            }
        }
    }

    public YamlConfiguration loadData(String data, String worldName) {
        return ConfigUtil.readData(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), worldName + File.separator + "customcrops_data" + File.separator + data +".yml"));
    }

    /**
     * Sprinkler Work
     * @param location sprinkler location
     */
    public void sprinklerWork(SimpleLocation location, Sprinkler sprinkler) {
        if (sprinkler.getWater() <= 0) return;
        Location sprinklerLoc = MiscUtils.getLocation(location);
        if (sprinklerLoc == null) return;
        sprinkler.setWater(sprinkler.getWater() - 1);
        int range = sprinkler.getRange();
        for(int i = -range; i <= range; i++){
            for (int j = -range; j <= range; j++){
                Location wetLoc = sprinklerLoc.clone().add(i,-1,j);
                cropManager.makePotWet(wetLoc);
                watered.add(MiscUtils.getSimpleLocation(wetLoc));
            }
        }
    }

    @Nullable
    public Fertilizer getFertilizer(Location potLoc) {
        return fertilizerCache.get(MiscUtils.getSimpleLocation(potLoc));
    }

    public boolean isPotWet(Location potLoc) {
        return watered.contains(MiscUtils.getSimpleLocation(potLoc));
    }

    public void removeFertilizer(Location potLoc) {
        fertilizerCache.remove(MiscUtils.getSimpleLocation(potLoc));
    }

    public void addFertilizer(Location potLoc, Fertilizer fertilizer) {
        fertilizerCache.put(MiscUtils.getSimpleLocation(potLoc), fertilizer);
    }

    public void removeCrop(Location cropLoc) {
        cropCache.remove(MiscUtils.getSimpleLocation(cropLoc));
    }

    @Nullable
    public Sprinkler getSprinkler(Location location) {
        return sprinklerCache.get(MiscUtils.getSimpleLocation(location));
    }

    public void addCrop(Location cropLoc, String crop) {
        cropCache.put(MiscUtils.getSimpleLocation(cropLoc), crop);
    }

    public void removeSprinkler(Location location) {
        sprinklerCache.remove(MiscUtils.getSimpleLocation(location));
    }

    public void addSprinkler(Location location, Sprinkler sprinkler) {
        sprinklerCache.put(MiscUtils.getSimpleLocation(location), sprinkler);
    }
}
