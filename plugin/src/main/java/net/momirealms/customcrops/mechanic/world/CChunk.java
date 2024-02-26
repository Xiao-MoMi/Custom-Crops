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

package net.momirealms.customcrops.mechanic.world;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.mechanic.world.block.MemoryPot;
import net.momirealms.customcrops.mechanic.world.block.MemorySprinkler;
import net.momirealms.customcrops.scheduler.task.CheckTask;
import net.momirealms.customcrops.scheduler.task.ReplaceTask;
import net.momirealms.customcrops.utils.RandomUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CChunk implements CustomCropsChunk {

    private transient CWorld cWorld;
    private final ChunkCoordinate chunkCoordinate;
    private final ConcurrentHashMap<SimpleLocation, WorldCrop> loadedCrops;
    private final ConcurrentHashMap<SimpleLocation, WorldSprinkler> loadedSprinklers;
    private final ConcurrentHashMap<SimpleLocation, WorldPot> loadedPots;
    private final PriorityQueue<CheckTask> queue;
    private final ArrayList<ReplaceTask> replaceTasks;
    private long lastLoadedTime;
    private int loadedSeconds;
    private int sprinklerTimes;
    private int potTimes;

    public CChunk(CWorld cWorld, ChunkCoordinate chunkCoordinate) {
        this.cWorld = cWorld;
        this.chunkCoordinate = chunkCoordinate;
        this.loadedCrops = new ConcurrentHashMap<>(64);
        this.loadedPots = new ConcurrentHashMap<>(64);
        this.loadedSprinklers = new ConcurrentHashMap<>(16);
        this.queue = new PriorityQueue<>();
        this.replaceTasks = new ArrayList<>();
        this.potTimes = 0;
        this.sprinklerTimes = 0;
    }

    public void setWorld(CWorld cWorld) {
        this.cWorld = cWorld;
    }

    @Override
    public CustomCropsWorld getCustomCropsWorld() {
        return cWorld;
    }

    @Override
    public ChunkCoordinate getChunkCoordinate() {
        return chunkCoordinate;
    }

    @Override
    public void secondTimer() {
        this.loadedSeconds++;

        int interval = cWorld.getWorldSetting().getPointInterval();
        // if loadedSeconds reach another recycle, rearrange the tasks
        if (this.loadedSeconds >= interval) {
            this.loadedSeconds = 0;
            this.queue.clear();
            this.arrangeTasks(cWorld.getWorldSetting());
        }

        // execute the tasks in queue
        while (!queue.isEmpty() && queue.peek().getTime() <= loadedSeconds) {
            CheckTask task = queue.poll();
            if (task != null)
                switch (task.getType()) {
                    case POT -> tickPots(task.getSimpleLocation());
                    case SPRINKLER -> tickSprinklers(task.getSimpleLocation());
                    case CROP -> tickCrops(task.getSimpleLocation());
                }
        }
    }

    @Override
    public int getLoadedSeconds() {
        return this.loadedSeconds;
    }

    @Override
    public void notifyUpdates() {
        if (this.replaceTasks.size() == 0)
            return;

        for (ReplaceTask replaceTask : this.replaceTasks) {

        }

        this.replaceTasks.clear();
    }

    public void arrangeTasks(WorldSetting setting) {
        int interval = setting.getPointInterval();
        for (Map.Entry<SimpleLocation, WorldCrop> entry : this.loadedCrops.entrySet()) {
            this.queue.add(new CheckTask(
                    RandomUtils.getRandomInt(0, interval),
                    CheckTask.TaskType.CROP,
                    entry.getKey()
            ));
        }

        this.potTimes++;
        this.sprinklerTimes++;

        if (this.potTimes >= setting.getTickPotInterval()) {
            this.potTimes = 0;
            for (Map.Entry<SimpleLocation, WorldPot> entry : this.loadedPots.entrySet()) {
                this.queue.add(new CheckTask(
                        RandomUtils.getRandomInt((int) ((double) interval * 0.05), interval),
                        CheckTask.TaskType.POT,
                        entry.getKey()
                ));
            }
        }

        if (this.sprinklerTimes >= setting.getTickSprinklerInterval()) {
            this.sprinklerTimes = 0;
            for (Map.Entry<SimpleLocation, WorldSprinkler> entry : this.loadedSprinklers.entrySet()) {
                this.queue.add(new CheckTask(
                        RandomUtils.getRandomInt(0, Math.max(1, (int) ((double) interval * 0.05))),
                        CheckTask.TaskType.SPRINKLER,
                        entry.getKey()
                ));
            }
        }
    }

    private void tickCrops(SimpleLocation location) {
        WorldCrop crop = loadedCrops.get(location);
        if (crop == null)
            return;

        // remove outdated crops
        Crop config = crop.getConfig();
        if (config == null) {
            loadedCrops.remove(location);
            return;
        }


    }

    private void tickPots(SimpleLocation location) {

    }

    private void tickSprinklers(SimpleLocation location) {

    }

    @Override
    public Optional<WorldCrop> getCropAt(SimpleLocation simpleLocation) {
        WorldCrop crop = loadedCrops.get(simpleLocation);
        return Optional.ofNullable(crop);
    }

    @Override
    public Optional<WorldSprinkler> getSprinklerAt(SimpleLocation simpleLocation) {
        WorldSprinkler sprinkler = loadedSprinklers.get(simpleLocation);
        return Optional.ofNullable(sprinkler);
    }

    @Override
    public Optional<WorldPot> getPotAt(SimpleLocation simpleLocation) {
        WorldPot pot = loadedPots.get(simpleLocation);
        return Optional.ofNullable(pot);
    }

    @Override
    public void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount) {
        Optional<WorldSprinkler> optionalSprinkler = getSprinklerAt(location);
        if (optionalSprinkler.isEmpty()) {
            loadedSprinklers.put(location, new MemorySprinkler(sprinkler.getKey(), amount, new HashMap<>()));
            CustomCropsPlugin.get().debug("When adding water to sprinkler at " + location + ", the sprinkler data doesn't exist.");
        } else {
            optionalSprinkler.get().setWater(optionalSprinkler.get().getWater() + amount);
        }
    }

    @Override
    public void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location) {
        Optional<WorldPot> optionalWorldPot = getPotAt(location);
        if (optionalWorldPot.isEmpty()) {
            MemoryPot memoryPot = new MemoryPot(pot.getKey(), new HashMap<>());
            memoryPot.setFertilizer(fertilizer.getKey());
            memoryPot.setFertilizerTimes(fertilizer.getTimes());
            loadedPots.put(location, memoryPot);
            CustomCropsPlugin.get().debug("When adding fertilizer to pot at " + location + ", the pot data doesn't exist.");
        } else {
            optionalWorldPot.get().setFertilizer(fertilizer.getKey());
            optionalWorldPot.get().setFertilizerTimes(fertilizer.getTimes());
        }
    }

    @Override
    public void addWaterToPot(Pot pot, SimpleLocation location, int amount) {
        Optional<WorldPot> optionalWorldPot = getPotAt(location);
        if (optionalWorldPot.isEmpty()) {
            MemoryPot memoryPot = new MemoryPot(pot.getKey(), new HashMap<>());
            memoryPot.setWater(amount);
            loadedPots.put(location, memoryPot);
            CustomCropsPlugin.get().debug("When adding water to pot at " + location + ", the pot data doesn't exist.");
        } else {
            optionalWorldPot.get().setWater(optionalWorldPot.get().getWater() + amount);
        }
    }

    @Override
    public void addPotAt(WorldPot pot, SimpleLocation location) {
        if (loadedPots.put(location, pot) != null) {
            CustomCropsPlugin.get().debug("Found duplicated pot data when adding pot at " + location);
        }
    }

    @Override
    public void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location) {
        if (loadedSprinklers.put(location, sprinkler) != null) {
            CustomCropsPlugin.get().debug("Found duplicated sprinkler data when adding sprinkler at " + location);
        }
    }

    @Override
    public void removeSprinklerAt(SimpleLocation location) {
        if (loadedSprinklers.remove(location) == null) {
            CustomCropsPlugin.get().debug("Failed to remove sprinkler from " + location + " because sprinkler doesn't exist.");
        }
    }

    @Override
    public void removePotAt(SimpleLocation location) {
        if (loadedPots.remove(location) == null) {
            CustomCropsPlugin.get().debug("Failed to remove pot from " + location + " because pot doesn't exist.");
        }
    }

    @Override
    public void removeCropAt(SimpleLocation location) {
        if (loadedCrops.remove(location) == null) {
            CustomCropsPlugin.get().debug("Failed to remove crop from " + location + " because crop doesn't exist.");
        }
    }

    @Override
    public int getCropAmount() {
        return loadedCrops.size();
    }

    @Override
    public int getPotAmount() {
        return loadedPots.size();
    }

    @Override
    public int getSprinklerAmount() {
        return loadedSprinklers.size();
    }
}
