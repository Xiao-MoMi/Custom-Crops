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

import com.google.gson.*;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.CustomWorldEvent;
import net.momirealms.customcrops.api.utils.SeasonUtils;
import net.momirealms.customcrops.config.*;
import net.momirealms.customcrops.integrations.season.CCSeason;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WorldState;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.MiscUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomWorld {

    private final World world;
    private final ConcurrentHashMap<SimpleLocation, String> cropCache;
    private final ConcurrentHashMap<SimpleLocation, Sprinkler> sprinklerCache;
    private final ConcurrentHashMap<SimpleLocation, Fertilizer> fertilizerCache;
    private final ConcurrentHashMap<String, HashSet<SimpleLocation>> scarecrowCache;
    private final Set<SimpleLocation> watered;
    private HashSet<SimpleLocation> tempWatered;
    private final HashSet<SimpleLocation> playerWatered;
    private final CropManager cropManager;
    private final HashSet<BukkitTask> tasksCache;
    private final BukkitScheduler bukkitScheduler;

    public CustomWorld(World world, CropManager cropManager) {
        this.world = world;
        this.cropCache = new ConcurrentHashMap<>(4096);
        this.fertilizerCache = new ConcurrentHashMap<>(2048);
        this.sprinklerCache = new ConcurrentHashMap<>(1024);
        this.tasksCache = new HashSet<>(4096);
        this.scarecrowCache = new ConcurrentHashMap<>(256);
        this.cropManager = cropManager;
        this.bukkitScheduler = Bukkit.getScheduler();
        this.watered = Collections.synchronizedSet(new HashSet<>());
        this.playerWatered = new HashSet<>();
        this.tempWatered = new HashSet<>();
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
            loadData();
            Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                CustomWorldEvent customWorldEvent = new CustomWorldEvent(world, WorldState.LOAD);
                Bukkit.getPluginManager().callEvent(customWorldEvent);
            });
        });
    }

    public void unload(boolean disable) {
        if (disable) {
            unloadData();
        }
        else {
            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
                unloadData();
                for (BukkitTask task : tasksCache) {
                    task.cancel();
                }
                tasksCache.clear();
                Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                    CustomWorldEvent customWorldEvent = new CustomWorldEvent(world, WorldState.UNLOAD);
                    Bukkit.getPluginManager().callEvent(customWorldEvent);
                });
            });
        }
    }

    private void loadData() {
        loadSeason();
        loadCropCache();
        loadSprinklerCache();
        loadFertilizerCache();
        loadPot();
        loadScarecrow();
    }

    private void unloadData() {
        unloadSeason();
        unloadCrop();
        unloadSprinkler();
        unloadFertilizer();
        unloadPot();
        unloadScarecrow();
        backUp(world.getName());
    }

    private void backUp(String worldName) {
        if (!MainConfig.autoBackUp) return;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            File file = new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), worldName + File.separator + "customcrops_data");
            File[] files = file.listFiles();
            if (files == null) return;
            for (File data : files) {
                FileUtils.copyFileToDirectory(data, new File(CustomCrops.plugin.getDataFolder(), "backup" + File.separator + worldName + "_" + format.format(date)));
            }
        }
        catch (IOException e){
            e.printStackTrace();
            AdventureUtil.consoleMessage("<red>[CustomCrops] Error! Failed to back up data for </red>");
        }
    }

    private void loadScarecrow() {
        if (!MainConfig.enableCrow) return;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement json= jsonParser.parse(new FileReader(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "scarecrow.json")));
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                for (Map.Entry<String, JsonElement> en : jsonObject.entrySet()) {
                    JsonArray jsonArray = en.getValue().getAsJsonArray();
                    int size = jsonArray.size();
                    HashSet<SimpleLocation> simpleLocations = new HashSet<>();
                    for (int i = 0; i < size; i++) {
                        simpleLocations.add(MiscUtils.getSimpleLocation(jsonArray.get(i).getAsString(), world.getName()));
                    }
                    scarecrowCache.put(en.getKey(), simpleLocations);
                }

            }
            SeasonUtils.setSeason(world, CCSeason.UNKNOWN);
        }
        catch (FileNotFoundException e) {
            //bypass
        }
    }

    private void unloadScarecrow() {
        if (!MainConfig.enableCrow) return;
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, HashSet<SimpleLocation>> entry : scarecrowCache.entrySet()) {
            HashSet<SimpleLocation> locations = entry.getValue();
            JsonArray jsonArray = new JsonArray();
            for (SimpleLocation simpleLocation : locations) {
                String loc = simpleLocation.getX() + "," + simpleLocation.getY() + "," + simpleLocation.getZ();
                jsonArray.add(new JsonPrimitive(loc));
            }
            jsonObject.add(entry.getKey(), jsonArray);
        }
        try (FileWriter fileWriter = new FileWriter(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "scarecrow.json"))){
            fileWriter.write(jsonObject.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SeasonUtils.unloadSeason(world);
    }

    private void loadSeason() {
        if (!SeasonConfig.enable) return;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement json= jsonParser.parse(new FileReader(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "season.json")));
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                if (jsonObject.has("season")) {
                    JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive("season");
                    String season = jsonPrimitive.getAsString();
                    if (MainConfig.realisticSeasonHook) return;
                    SeasonUtils.setSeason(world, CCSeason.valueOf(season));
                    return;
                }
            }
            SeasonUtils.setSeason(world, CCSeason.UNKNOWN);
        }
        catch (FileNotFoundException e) {
            //bypass
        }
    }

    private void unloadSeason() {
        if (!SeasonConfig.enable) return;
        JsonObject jsonObject = new JsonObject();
        JsonPrimitive jsonPrimitive = new JsonPrimitive(SeasonUtils.getSeason(world).name());
        jsonObject.add("season", jsonPrimitive);
        try (FileWriter fileWriter = new FileWriter(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "season.json"))){
            fileWriter.write(jsonObject.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SeasonUtils.unloadSeason(world);
    }

    private void loadPot() {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement json= jsonParser.parse(new FileReader(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "pot.json")));
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                if (jsonObject.has("pot")) {
                    JsonArray jsonArray = jsonObject.getAsJsonArray("pot");
                    String name = world.getName();
                    for (JsonElement jsonElement : jsonArray) {
                        String loc = jsonElement.getAsString();
                        String[] locs = StringUtils.split(loc, ",");
                        watered.add(new SimpleLocation(name, Integer.parseInt(locs[0]), Integer.parseInt(locs[1]), Integer.parseInt(locs[2])));
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            //bypass
        }
    }

    private void unloadPot() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        watered.addAll(playerWatered);
        for (SimpleLocation simpleLocation : watered) {
            jsonArray.add(simpleLocation.getX() + "," + simpleLocation.getY() + "," + simpleLocation.getZ());
        }
        watered.clear();
        jsonObject.add("pot", jsonArray);
        try (FileWriter fileWriter = new FileWriter(new File(CustomCrops.plugin.getDataFolder().getParentFile().getParentFile(), world.getName() + File.separator + "customcrops_data" + File.separator + "pot.json"))){
            fileWriter.write(jsonObject.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
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

    public void growWire(int cropTime, int sprinklerTime, int dryTime, boolean compensation) {

        Random randomGenerator = new Random();

        if (!compensation) {
            route(sprinklerTime);
            potDryJudge(sprinklerTime + randomGenerator.nextInt(dryTime));
        }

        CropModeInterface cropMode = cropManager.getCropMode();

        for (SimpleLocation location : cropCache.keySet()) {
            BukkitTask task = bukkitScheduler.runTaskLaterAsynchronously(CustomCrops.plugin, () -> {
                Location seedLoc = MiscUtils.getLocation(location);
                if (seedLoc == null) return;
                if (cropMode.growJudge(seedLoc)) {
                    cropCache.remove(location);
                }
            }, sprinklerTime + dryTime + randomGenerator.nextInt(cropTime));
            tasksCache.add(task);
        }
    }

    public void growFrame(int cropTime, int sprinklerTime, int dryTime, boolean compensation) {

        Random randomGenerator = new Random();

        if (!compensation) {
            route(sprinklerTime);
            potDryJudge(sprinklerTime + randomGenerator.nextInt(dryTime));
        }

        CropModeInterface cropMode = cropManager.getCropMode();

        for (SimpleLocation location : cropCache.keySet()) {
            long random = randomGenerator.nextInt(cropTime);
            BukkitTask task1 = bukkitScheduler.runTaskLater(CustomCrops.plugin, () -> {
                Location seedLoc = MiscUtils.getLocation(location);
                if (seedLoc == null) return;
                cropMode.loadChunk(seedLoc);
            }, random + dryTime + random);
            BukkitTask task2 = bukkitScheduler.runTaskLater(CustomCrops.plugin, () -> {
                Location seedLoc = MiscUtils.getLocation(location);
                if (seedLoc == null) return;
                if (cropMode.growJudge(seedLoc)) {
                    cropCache.remove(location);
                }
            }, sprinklerTime + dryTime + random + 5);
            tasksCache.add(task1);
            tasksCache.add(task2);
        }
    }

    private void route(int sprinklerTime) {

        for (BukkitTask task : tasksCache) {
            task.cancel();
        }
        tasksCache.clear();

        tempWatered = new HashSet<>(watered);
        watered.clear();
        watered.addAll(playerWatered);
        playerWatered.clear();

        Random randomGenerator = new Random();
        for (Map.Entry<SimpleLocation, Sprinkler> sprinklerEntry : sprinklerCache.entrySet()) {

            BukkitTask task = bukkitScheduler.runTaskLaterAsynchronously(CustomCrops.plugin, () -> {
                sprinklerWork(sprinklerEntry.getKey(), sprinklerEntry.getValue());
            }, randomGenerator.nextInt(sprinklerTime));

            tasksCache.add(task);
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

        if (MainConfig.enableAnimations) {
            Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                for (Player player : sprinklerLoc.getNearbyPlayers(48)) {
                    cropManager.getArmorStandUtil().playWaterAnimation(player, sprinklerLoc.clone().add(0.5, 0.3, 0.5));
                }
            });
        }

        sprinkler.setWater(sprinkler.getWater() - 1);
        int range = sprinkler.getRange();
        for(int i = -range; i <= range; i++){
            for (int j = -range; j <= range; j++){
                Location wetLoc = sprinklerLoc.clone().add(i,-1,j);
                cropManager.makePotWet(wetLoc);
                setPotWet(wetLoc);
            }
        }
    }

    private void potDryJudge(int time) {
        bukkitScheduler.runTaskLater(CustomCrops.plugin, () -> {
            tempWatered.removeAll(watered);
            for (SimpleLocation simpleLocation : tempWatered) {
                Location dryLoc = MiscUtils.getLocation(simpleLocation);
                if (dryLoc == null) return;
                cropManager.potDryJudge(dryLoc);
            }
        }, time);
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

    public void removeWatered(Location potLoc) {
        watered.remove(MiscUtils.getSimpleLocation(potLoc));
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

    public void setPotWet(Location location) {
        watered.add(MiscUtils.getSimpleLocation(location));
    }

    public void setPlayerWatered(Location location) {
        playerWatered.add(MiscUtils.getSimpleLocation(location));
    }

    public boolean hasScarecrow(Location location) {
        Chunk chunk = location.getChunk();
        return scarecrowCache.containsKey(chunk.getX() + "," + chunk.getZ());
    }

    public void addScarecrow(Location location) {
        Chunk chunk = location.getChunk();
        HashSet<SimpleLocation> old = scarecrowCache.get(chunk.getX() + "," + chunk.getZ());
        if (old == null) {
            HashSet<SimpleLocation> young = new HashSet<>(4);
            young.add(new SimpleLocation(world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            scarecrowCache.put(chunk.getX() + "," + chunk.getZ(), young);
        }
        else {
            old.add(new SimpleLocation(world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }

    public void removeScarecrow(Location location) {
        Chunk chunk = location.getChunk();
        HashSet<SimpleLocation> old = scarecrowCache.get(chunk.getX() + "," + chunk.getZ());
        if (old == null) return;
        old.remove(new SimpleLocation(world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (old.size() == 0) scarecrowCache.remove(chunk.getX() + "," + chunk.getZ());
    }
}
