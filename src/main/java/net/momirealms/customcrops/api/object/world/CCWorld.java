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
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.action.Action;
import net.momirealms.customcrops.api.object.action.VariationImpl;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.condition.Condition;
import net.momirealms.customcrops.api.object.condition.DeathCondition;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.crop.StageConfig;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.object.fertilizer.SoilRetain;
import net.momirealms.customcrops.api.object.fertilizer.SpeedGrow;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.pot.PotConfig;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.season.SeasonData;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import net.momirealms.customcrops.api.object.sprinkler.SprinklerAnimation;
import net.momirealms.customcrops.api.object.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.util.AdventureUtils;
import net.momirealms.customcrops.api.util.ConfigUtils;
import net.momirealms.customcrops.api.util.FakeEntityUtils;
import net.momirealms.customcrops.api.util.RotationUtils;
import net.momirealms.customcrops.helper.Log;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;

public class CCWorld extends Function {

    private final String worldName;
    private final Reference<World> world;
    private final ConcurrentHashMap<ChunkCoordinate, CCChunk> chunkMap;
    private final ScheduledThreadPoolExecutor schedule;
    private long currentDay;
    private ScheduledFuture<?> timerTask;
    private int pointTimer;
    private int cacheTimer;
    private int workCounter;
    private int consumeCounter;
    private final Set<SimpleLocation> plantInPoint;
    private Set<ChunkCoordinate> loadInPoint;
    private final ConcurrentHashMap<SimpleLocation, String> corruptedPot;
    private final File chunksFolder;
    private final File dateFile;
    private final File corruptedFile;
    private final CustomCrops plugin;

    public CCWorld(World world, CustomCrops plugin) {
        this.plugin = plugin;
        this.worldName = world.getName();
        this.chunksFolder = ConfigUtils.getFile(world, "chunks");
        this.dateFile = ConfigUtils.getFile(world, "data.yml");
        this.corruptedFile = ConfigUtils.getFile(world, "corrupted.yml");
        this.world = new WeakReference<>(world);
        this.chunkMap = new ConcurrentHashMap<>(64);
        this.schedule = new ScheduledThreadPoolExecutor(ConfigManager.corePoolSize);
        this.schedule.setMaximumPoolSize(ConfigManager.maxPoolSize);
        this.schedule.setKeepAliveTime(ConfigManager.keepAliveTime, TimeUnit.SECONDS);
        this.schedule.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        this.plantInPoint = Collections.synchronizedSet(new HashSet<>(128));
        this.loadInPoint = Collections.synchronizedSet(new HashSet<>(32));
        this.corruptedPot = new ConcurrentHashMap<>(128);
        this.cacheTimer = ConfigManager.cacheSaveInterval;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void init() {
        loadDateData();
        loadCorruptedPots();
        if (!chunksFolder.exists()) chunksFolder.mkdirs();
        if (!ConfigManager.onlyInLoadedChunks) {
            loadAllChunkData();
        }
    }

    @Override
    public void disable() {
        closePool();
        saveDateData();
        saveCorruptedPots();
        saveAllChunkData();
        plugin.getSeasonManager().unloadSeasonData(worldName);
    }

    public void load() {
        this.pointTimer = ConfigManager.pointGainInterval;
        this.cacheTimer = ConfigManager.cacheSaveInterval;
        this.consumeCounter = ConfigManager.intervalConsume;
        this.workCounter = ConfigManager.intervalWork;
        this.scheduleTask();
    }

    public void unload() {
        if (this.timerTask != null) {
            this.timerTask.cancel(false);
            this.timerTask = null;
        }
    }

    public void loadCorruptedPots() {
        YamlConfiguration dataFile = ConfigUtils.readData(corruptedFile);
        for (Map.Entry<String, Object> entry : dataFile.getValues(false).entrySet()) {
            corruptedPot.put(SimpleLocation.getByString(entry.getKey(), worldName), (String) entry.getValue());
        }
    }

    public void saveCorruptedPots() {
        YamlConfiguration dataFile = new YamlConfiguration();
        for (Map.Entry<SimpleLocation, String> entry : corruptedPot.entrySet()) {
            SimpleLocation simpleLocation = entry.getKey();
            dataFile.set(simpleLocation.getX() + "," + simpleLocation.getY() + "," + simpleLocation.getZ(), entry.getValue());
        }
        try {
            dataFile.save(corruptedFile);
        } catch (IOException e) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Failed to save corrupted data for world: " + worldName);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadAllChunkData() {
        File[] data_files = chunksFolder.listFiles();
        if (data_files == null) return;
        List<File> outdated = new ArrayList<>();
        for (File file : data_files) {
            ChunkCoordinate chunkCoordinate = ChunkCoordinate.getByString(file.getName().substring(0, file.getName().length() - 7));
            try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
                CCChunk chunk = (CCChunk) ois.readObject();
                if (chunk.isUseless()) {
                    outdated.add(file);
                    continue;
                }
                if (chunkCoordinate != null) chunkMap.put(chunkCoordinate, chunk);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Log.info("Error at " + file.getAbsolutePath());
                outdated.add(file);
            }
        }
        for (File file : outdated) {
            file.delete();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveAllChunkData() {
        if (!chunksFolder.exists()) chunksFolder.mkdirs();
        for (Map.Entry<ChunkCoordinate, CCChunk> entry : chunkMap.entrySet()) {
            ChunkCoordinate chunkCoordinate = entry.getKey();
            CCChunk chunk = entry.getValue();
            String fileName = chunkCoordinate.getFileName() + ".ccdata";
            File file = new File(chunksFolder, fileName);
            if (chunk.isUseless() && file.exists()) {
                file.delete();
                continue;
            }
            try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(chunk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveDateData() {
        YamlConfiguration dataFile = new YamlConfiguration();
        if (ConfigManager.enableSeason && !ConfigManager.rsHook) {
            SeasonData seasonData = plugin.getSeasonManager().getSeasonData(worldName);
            if (seasonData == null) {
                dataFile.set("season", "SPRING");
                dataFile.set("date", 1);
            } else {
                dataFile.set("season", seasonData.getSeason().name());
                dataFile.set("date", seasonData.getDate());
            }
        }
        dataFile.set("day", currentDay);
        try {
            dataFile.save(dateFile);
        } catch (IOException e) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Failed to save season data for world: " + worldName);
        }
    }

    public void loadDateData() {
        YamlConfiguration dataFile = ConfigUtils.readData(dateFile);
        if (ConfigManager.enableSeason) {
            SeasonData seasonData;
            if (dataFile.contains("season") && dataFile.contains("date")) {
                seasonData = new SeasonData(worldName, CCSeason.valueOf(dataFile.getString("season")), dataFile.getInt("date"));
            } else {
                seasonData = new SeasonData(worldName);
            }
            plugin.getSeasonManager().loadSeasonData(seasonData);
        }
        this.currentDay = dataFile.getLong("day", 0);
    }

    private void scheduleTask() {
        if (this.timerTask == null) {
            this.timerTask = plugin.getScheduler().runTaskTimerAsync(() -> {
                World current = world.get();
                if (current != null) {
                    if (ConfigManager.debugScheduler) {
                        Log.info("Queue size: " + schedule.getQueue().size() + " Completed: " + schedule.getCompletedTaskCount());
                    }
                    long day = current.getFullTime() / 24000;
                    long time = current.getTime();
                    this.tryDayCycleTask(time, day);
                    this.timerTask();
                } else {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] World: " + worldName + " unloaded unexpectedly. Shutdown the schedule.");
                    this.schedule.shutdown();
                }
            }, 1000, 1000L);
        }
    }

    private void tryDayCycleTask(long time, long day) {
        if (time < 100 && day != currentDay) {
            currentDay = day;
            if (ConfigManager.enableSeason && !ConfigManager.rsHook && ConfigManager.autoSeasonChange) {
                plugin.getSeasonManager().addDate(worldName);
            }
        }
        if (ConfigManager.cacheSaveInterval != -1) {
            cacheTimer--;
            if (cacheTimer <= 0) {
                if (ConfigManager.debugScheduler) Log.info("== Save cache ==");
                cacheTimer = ConfigManager.cacheSaveInterval;
                schedule.execute(this::saveDateData);
                schedule.execute(this::saveCorruptedPots);
                schedule.execute(this::saveAllChunkData);
            }
        }
    }

    private void timerTask() {
        pointTimer--;
        if (pointTimer <= 0) {
            pointTimer = ConfigManager.pointGainInterval;
            onReachPoint();
        }
    }

    public void onReachPoint() {
        if (ConfigManager.debugScheduler) Log.info("== Grow point ==");
        if (ConfigManager.enableScheduleSystem) {
            // clear the locations where crops are planted in a point interval
            plantInPoint.clear();
            // clear the chunk coordinates that has grown in a point interval
            loadInPoint = Collections.synchronizedSet(new HashSet<>(chunkMap.keySet()));
            // clear the queue if there exists unhandled tasks
            schedule.getQueue().clear();
            // arrange crop grow check task
            for (CCChunk chunk : chunkMap.values()) {
                chunk.scheduleGrowTask(this, -1);
            }
            workCounter--;
            consumeCounter--;
            if (consumeCounter == 0) {
                if (ConfigManager.debugScheduler) Log.info("== Consume time ==");
                consumeCounter = ConfigManager.intervalConsume;
                scheduleConsumeTask(-1);
            }
            if (workCounter == 0) {
                if (ConfigManager.debugScheduler) Log.info("== Work time ==");
                workCounter = ConfigManager.intervalWork;
                scheduleSprinklerWork(-1);
            }
        }
    }

    private void closePool() {
        this.schedule.shutdown();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void unloadChunk(ChunkCoordinate chunkCoordinate) {
        if (!ConfigManager.onlyInLoadedChunks) return;
        CCChunk chunk = chunkMap.remove(chunkCoordinate);
        if (chunk != null) {
            File file = new File(chunksFolder, chunkCoordinate.getFileName() + ".ccdata");
            if (chunk.isUseless() && file.exists()) {
                file.delete();
                return;
            }
            try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(chunk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadChunk(ChunkCoordinate chunkCoordinate) {
        if (!ConfigManager.onlyInLoadedChunks) return;
        File file = new File(chunksFolder, chunkCoordinate.getFileName() + ".ccdata");
        if (file.exists()) {
            boolean delete = false;
            try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
                CCChunk chunk = (CCChunk) ois.readObject();
                if (chunk.isUseless()) {
                    delete = true;
                } else {
                    chunkMap.put(chunkCoordinate, chunk);
                    if (!loadInPoint.contains(chunkCoordinate)) {
                        chunk.scheduleGrowTask(this, -1);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Log.info("Error at " + file.getAbsolutePath());
            } finally {
                if (delete) {
                    file.delete();
                }
            }
        }
    }

    public void pushCropTask(SimpleLocation simpleLocation, int delay) {
        schedule.schedule(new CropCheckTask(simpleLocation), delay, TimeUnit.MILLISECONDS);
    }

    public void pushSprinklerTask(SimpleLocation simpleLocation, int delay) {
        schedule.schedule(new SprinklerCheckTask(simpleLocation), delay, TimeUnit.MILLISECONDS);
    }

    public void pushConsumeTask(SimpleLocation simpleLocation, int delay) {
        schedule.schedule(new ConsumeCheckTask(simpleLocation), delay, TimeUnit.MILLISECONDS);
    }

    public String removeCorrupted(SimpleLocation simpleLocation) {
        return corruptedPot.remove(simpleLocation);
    }

    public void fixCorruptedData() {
        for (SimpleLocation simpleLocation : corruptedPot.keySet()) {
            CustomCrops.getInstance().getScheduler().runTaskAsyncLater(new FixTask(simpleLocation), ThreadLocalRandom.current().nextInt(30000));
        }
    }

    public class FixTask implements Runnable {

        private final SimpleLocation simpleLocation;

        public FixTask(SimpleLocation simpleLocation) {
            this.simpleLocation = simpleLocation;
        }

        @Override
        public void run() {
            String key = corruptedPot.remove(simpleLocation);
            PotConfig potConfig = plugin.getPotManager().getPotConfig(key);
            if (potConfig == null) return;
            Pot pot = getPotData(simpleLocation);
            boolean wet = false;
            Fertilizer fertilizer = null;
            if (pot != null) {
                wet = pot.isWet();
                fertilizer = pot.getFertilizer();
            }
            Location location = simpleLocation.getBukkitLocation();
            if (location == null) return;
            String replacer = wet ? potConfig.getWetPot(fertilizer) : potConfig.getDryPot(fertilizer);
            CompletableFuture<Chunk> asyncGetChunk = location.getWorld().getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4);
            asyncGetChunk.whenComplete((result, throwable) ->
                plugin.getScheduler().runTask(() -> plugin.getPlatformInterface().placeNoteBlock(location, replacer)
            ));
        }
    }

    public class ConsumeCheckTask implements Runnable {

        private final SimpleLocation simpleLocation;

        public ConsumeCheckTask(SimpleLocation simpleLocation) {
            this.simpleLocation = simpleLocation;
        }

        public void run() {

            Pot pot = getPotData(simpleLocation);
            if (pot == null) return;

            if (pot.isWet() && plugin.getFertilizerManager().getConfigByFertilizer(pot.getFertilizer()) instanceof SoilRetain soilRetain && soilRetain.canTakeEffect()) {
                pot.setWater(pot.getWater() + 1);
            }

            if (pot.reduceWater() | pot.reduceFertilizer()) {

                Fertilizer fertilizer = pot.getFertilizer();
                boolean wet = pot.isWet();
                if (!wet && fertilizer == null) {
                    removePotData(simpleLocation);
                }

                Location location = simpleLocation.getBukkitLocation();
                if (location == null) {
                    return;
                }

                PotConfig potConfig = pot.getConfig();
                if (wet && fertilizer == null && !potConfig.isEnableFertilized()) {
                    return;
                }

                CompletableFuture<Chunk> asyncGetChunk = location.getWorld().getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                asyncGetChunk.whenComplete((result, throwable) ->
                    plugin.getScheduler().runTask(() -> {
                        Block block = location.getBlock();
                        if (block.getType() == Material.AIR) {
                            removePotData(simpleLocation);
                            return;
                        }
                        String replacer = wet ? potConfig.getWetPot(fertilizer) : potConfig.getDryPot(fertilizer);
                        String id = plugin.getPlatformInterface().getBlockID(block);
                        if (ConfigManager.enableCorruptionFixer && id.equals("NOTE_BLOCK")) {
                            plugin.getPlatformInterface().placeNoteBlock(location, replacer);
                            return;
                        }
                        String potKey = plugin.getPotManager().getPotKeyByBlockID(id);
                        if (potKey == null) {
                            removePotData(simpleLocation);
                            return;
                        }
                        if (!potKey.equals(pot.getPotKey())) {
                            return;
                        }
                        if (ConfigUtils.isVanillaItem(replacer)) {
                            block.setType(Material.valueOf(replacer));
                            if (block.getBlockData() instanceof Farmland farmland && ConfigManager.disableMoistureMechanic) {
                                farmland.setMoisture(wet ? farmland.getMaximumMoisture() : 0);
                                block.setBlockData(farmland);
                            }
                        } else {
                            plugin.getPlatformInterface().placeNoteBlock(location, replacer);
                        }
                    }
                ));
            }
        }
    }

    public class SprinklerCheckTask implements Runnable {

        private final SimpleLocation simpleLocation;

        public SprinklerCheckTask(SimpleLocation simpleLocation) {
            this.simpleLocation = simpleLocation;
        }

        public void run() {
            Sprinkler sprinkler = getSprinklerData(simpleLocation);
            if (sprinkler == null) return;

            SprinklerConfig sprinklerConfig = sprinkler.getConfig();
            if (sprinklerConfig == null) {
                removeSprinklerData(simpleLocation);
                return;
            }

            int water = sprinkler.getWater();
            sprinkler.setWater(--water);
            if (water <= 0) {
                removeSprinklerData(simpleLocation);
            }

            SprinklerAnimation sprinklerAnimation = sprinklerConfig.getSprinklerAnimation();
            Location location = simpleLocation.getBukkitLocation();
            if (location != null && sprinklerAnimation != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    SimpleLocation playerLoc = SimpleLocation.getByBukkitLocation(player.getLocation());
                    if (playerLoc.isNear(simpleLocation, 48)) {
                        FakeEntityUtils.playWaterAnimation(player, location.clone().add(0.5, sprinklerAnimation.offset(), 0.5), sprinklerAnimation.id(), sprinklerAnimation.duration(), sprinklerAnimation.itemMode());
                    }
                }
            }

            int range = sprinklerConfig.getRange();
            int amount = sprinklerConfig.getWaterFillAbility();
            int random = sprinklerAnimation == null ? 10000 : sprinklerAnimation.duration() * 1000;
            String[] whiteList = sprinklerConfig.getPotWhitelist();
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j <= range; j++) {
                    SimpleLocation potSLoc = simpleLocation.add(i, -1, j);
                    schedule.schedule(new WaterPotTask(potSLoc, amount, whiteList), ThreadLocalRandom.current().nextInt(random), TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public class WaterPotTask implements Runnable {

        @NotNull
        private final SimpleLocation simpleLocation;
        private final int amount;
        @Nullable
        private final String[] whitelist;

        public WaterPotTask(@NotNull SimpleLocation simpleLocation, int amount, @Nullable String[] whitelist) {
            this.simpleLocation = simpleLocation;
            this.amount = amount;
            this.whitelist = whitelist;
        }

        @Override
        public void run() {
            Location location = simpleLocation.getBukkitLocation();
            if (location == null) return;
            CompletableFuture<Chunk> asyncGetChunk = location.getWorld().getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4);
            asyncGetChunk.whenComplete((result, throwable) ->
                plugin.getScheduler().runTask(() -> {
                    String blockID = plugin.getPlatformInterface().getBlockID(location.getBlock());
                    String potKey = plugin.getPotManager().getPotKeyByBlockID(blockID);
                    if (potKey != null) {
                        if (whitelist != null) {
                            for (String pot : whitelist) {
                                if (pot.equals(potKey)) {
                                    addWaterToPot(simpleLocation, amount, potKey);
                                    break;
                                }
                            }
                        } else {
                            addWaterToPot(simpleLocation, amount, potKey);
                        }
                    } else if (ConfigManager.enableCorruptionFixer && blockID.equals("NOTE_BLOCK")) {
                        Pot pot = getPotData(simpleLocation);
                        if (pot != null) {
                            // mark it as corrupted
                            potKey = pot.getPotKey();
                            if (whitelist == null) {
                                pot.addWater(amount);
                            } else {
                                for (String potID : whitelist) {
                                    if (potID.equals(potKey)) {
                                        pot.addWater(amount);
                                        break;
                                    }
                                }
                            }
                            corruptedPot.put(simpleLocation, potKey);
                            if (ConfigManager.debugCorruption) AdventureUtils.consoleMessage("[CustomCrops] Corrupted pot found at: " + simpleLocation);
                            // only custom blocks would corrupt
                            // so it's not necessary to check if the pot is a vanilla block
                            // String replacer = pot.isWet() ? pot.getConfig().getWetPot(pot.getFertilizer()) : pot.getConfig().getDryPot(pot.getFertilizer());
                            // plugin.getPlatformInterface().placeNoteBlock(location, replacer);
                        }
                    }
                }
            ));
        }
    }

    public class CropCheckTask implements Runnable {

        private final SimpleLocation simpleLocation;

        public CropCheckTask(SimpleLocation simpleLocation) {
            this.simpleLocation = simpleLocation;
        }

        public void run() {
            GrowingCrop growingCrop = getCropData(simpleLocation);
            if (growingCrop == null) return;

            CropConfig cropConfig = growingCrop.getConfig();
            if (cropConfig == null) {
                removeCropData(simpleLocation);
                return;
            }

            ItemMode itemMode = cropConfig.getCropMode();
            DeathCondition[] deathConditions = cropConfig.getDeathConditions();
            if (deathConditions != null) {
                for (DeathCondition deathCondition : deathConditions) {
                    if (deathCondition.checkIfDead(simpleLocation)) {
                        removeCropData(simpleLocation);
                        deathCondition.applyDeadModel(simpleLocation, itemMode);
                        return;
                    }
                }
            }

            Condition[] conditions = cropConfig.getGrowConditions();
            if (conditions != null) {
                for (Condition condition : conditions) {
                    if (!condition.isMet(simpleLocation)) {
                        return;
                    }
                }
            }

            int points = 1;
            Pot pot = getPotData(simpleLocation.add(0,-1,0));
            if (pot != null) {
                FertilizerConfig fertilizerConfig = plugin.getFertilizerManager().getConfigByFertilizer(pot.getFertilizer());
                if (fertilizerConfig instanceof SpeedGrow speedGrow) {
                    points += speedGrow.getPointBonus();
                }
            }
            addCropPoint(points, cropConfig, growingCrop, simpleLocation, itemMode);
        }
    }

    public boolean addCropPointAt(SimpleLocation simpleLocation, int points) {
        GrowingCrop growingCrop = getCropData(simpleLocation);
        if (growingCrop == null) return false;
        CropConfig cropConfig = growingCrop.getConfig();
        if (cropConfig == null) {
            removeCropData(simpleLocation);
            return false;
        }
        if (points == 0) return true;
        addCropPoint(points, cropConfig, growingCrop, simpleLocation, cropConfig.getCropMode());
        return true;
    }

    public void addCropPoint(int points, CropConfig cropConfig, GrowingCrop growingCrop, SimpleLocation simpleLocation, ItemMode itemMode) {
        int current = growingCrop.getPoints();
        String nextModel = null;
        for (int i = current + 1; i <= points + current; i++) {
            StageConfig stageConfig = cropConfig.getStageConfig(i);
            if (stageConfig == null) continue;
            if (stageConfig.getModel() != null) nextModel = stageConfig.getModel();
            Action[] growActions = stageConfig.getGrowActions();
            if (growActions != null) {
                for (Action action : growActions) {
                    if (action instanceof VariationImpl variation) {
                        if (variation.doOn(simpleLocation, itemMode)) {
                            return;
                        }
                    } else {
                        action.doOn(null, simpleLocation, itemMode);
                    }
                }
            }
        }

        growingCrop.setPoints(current + points);
        if (growingCrop.getPoints() >= cropConfig.getMaxPoints()) {
            removeCropData(simpleLocation);
        }

        Location location = simpleLocation.getBukkitLocation();
        String finalNextModel = nextModel;
        if (finalNextModel == null || location == null) return;

        CompletableFuture<Chunk> asyncGetChunk = location.getWorld().getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        if (itemMode == ItemMode.ITEM_FRAME) {
            CompletableFuture<Boolean> loadEntities = asyncGetChunk.thenApply((chunk) -> {
                chunk.getEntities();
                return chunk.isEntitiesLoaded();
            });
            loadEntities.whenComplete((result, throwable) ->
                    plugin.getScheduler().runTask(() -> {
                        if (plugin.getPlatformInterface().removeCustomItem(location, itemMode)) {
                            ItemFrame itemFrame = plugin.getPlatformInterface().placeItemFrame(location, finalNextModel);
                            if (itemFrame != null && cropConfig.isRotationEnabled()) itemFrame.setRotation(RotationUtils.getRandomRotation());
                        } else {
                            removeCropData(simpleLocation);
                        }
            }));
        } else if (itemMode == ItemMode.ITEM_DISPLAY) {
            CompletableFuture<Boolean> loadEntities = asyncGetChunk.thenApply((chunk) -> {
                chunk.getEntities();
                return chunk.isEntitiesLoaded();
            });
            loadEntities.whenComplete((result, throwable) ->
                    plugin.getScheduler().runTask(() -> {
                        if (plugin.getPlatformInterface().removeCustomItem(location, itemMode)) {
                            ItemDisplay itemDisplay = plugin.getPlatformInterface().placeItemDisplay(location, finalNextModel);
                            if (itemDisplay != null && cropConfig.isRotationEnabled()) itemDisplay.setRotation(RotationUtils.getRandomFloatRotation(), 0);
                        } else {
                            removeCropData(simpleLocation);
                        }
                    }));
        } else {
            asyncGetChunk.whenComplete((result, throwable) ->
                    plugin.getScheduler().runTask(() -> {
                        if (plugin.getPlatformInterface().removeCustomItem(location, itemMode)) {
                            plugin.getPlatformInterface().placeTripWire(location, finalNextModel);
                        } else {
                            removeCropData(simpleLocation);
                        }
                    }));
        }
    }

    public World getWorld() {
        return world.get();
    }

    public void removePotData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removePotData(simpleLocation);
    }

    public void removeCropData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeCropData(simpleLocation);
    }

    public void addCropData(SimpleLocation simpleLocation, GrowingCrop growingCrop, boolean grow) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addCropData(simpleLocation, growingCrop);
            if (grow) growIfNotDuplicated(simpleLocation);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addCropData(simpleLocation, growingCrop);
        if (grow) growIfNotDuplicated(simpleLocation);
    }

    private void growIfNotDuplicated(SimpleLocation simpleLocation) {
        if (plantInPoint.contains(simpleLocation)) {
            return;
        }
        pushCropTask(simpleLocation, ThreadLocalRandom.current().nextInt(ConfigManager.pointGainInterval * 1000));
        plantInPoint.add(simpleLocation);
    }

    public GrowingCrop getCropData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            return chunk.getCropData(simpleLocation);
        }
        return null;
    }

    public int getChunkCropAmount(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return 0;
        return chunk.getCropAmount();
    }

    public void removeGreenhouse(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeGreenhouse(simpleLocation);
    }

    public void addGreenhouse(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addGreenhouse(simpleLocation);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addGreenhouse(simpleLocation);
    }

    public boolean isGreenhouse(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return false;
        return chunk.isGreenhouse(simpleLocation);
    }

    public void removeScarecrow(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeScarecrow(simpleLocation);
    }

    public void addScarecrow(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addScarecrow(simpleLocation);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addScarecrow(simpleLocation);
    }

    public boolean hasScarecrow(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return false;
        return chunk.hasScarecrow();
    }

    public void removeSprinklerData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeSprinklerData(simpleLocation);
    }

    public void addSprinklerData(SimpleLocation simpleLocation, Sprinkler sprinkler) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addSprinklerData(simpleLocation, sprinkler);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addSprinklerData(simpleLocation, sprinkler);
    }

    @Nullable
    public Sprinkler getSprinklerData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return null;
        return chunk.getSprinklerData(simpleLocation);
    }

    public void addWaterToPot(SimpleLocation simpleLocation, int amount, @NotNull String pot_id) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addWaterToPot(simpleLocation, amount, pot_id);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addWaterToPot(simpleLocation, amount, pot_id);
    }

    public void addFertilizerToPot(SimpleLocation simpleLocation, Fertilizer fertilizer, @NotNull String pot_id) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addFertilizerToPot(simpleLocation, fertilizer, pot_id);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addFertilizerToPot(simpleLocation, fertilizer, pot_id);
    }

    public Pot getPotData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return null;
        return chunk.getPotData(simpleLocation);
    }

    public void addPotData(SimpleLocation simpleLocation, Pot pot) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addPotData(simpleLocation, pot);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addPotData(simpleLocation, pot);
    }

    public CCChunk createNewChunk(SimpleLocation simpleLocation) {
        CCChunk newChunk = new CCChunk();
        chunkMap.put(simpleLocation.getChunkCoordinate(), newChunk);
        return newChunk;
    }

    public void scheduleSprinklerWork(int force) {
        schedule.execute(() -> {
            for (CCChunk chunk : chunkMap.values()) {
                chunk.scheduleSprinklerTask(this, force);
            }
        });
    }

    public void scheduleConsumeTask(int force) {
        schedule.execute(() -> {
            for (CCChunk chunk : chunkMap.values()) {
                chunk.scheduleConsumeTask(this, force);
            }
        });
    }

    public void scheduleCropGrowTask(int force) {
        schedule.execute(() -> {
            for (CCChunk chunk : chunkMap.values()) {
                chunk.scheduleGrowTask(this, force);
            }
        });
    }

    @Nullable
    public String getCorruptedPotOriginalKey(SimpleLocation simpleLocation) {
        return corruptedPot.get(simpleLocation);
    }
}