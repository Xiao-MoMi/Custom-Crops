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
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.mechanic.world.block.MemoryCrop;
import net.momirealms.customcrops.mechanic.world.block.MemoryPot;
import net.momirealms.customcrops.mechanic.world.block.MemorySprinkler;
import net.momirealms.customcrops.scheduler.task.CheckTask;
import net.momirealms.customcrops.scheduler.task.ReplaceTask;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CChunk implements CustomCropsChunk {

    private transient CWorld cWorld;
    private ChunkCoordinate chunkCoordinate;
    private ConcurrentHashMap<ChunkPos, CustomCropsBlock> loadedBlocks;
    private PriorityQueue<CheckTask> queue;
    private ArrayList<ReplaceTask> replaceTasks;
    private long lastLoadedTime;
    private int loadedSeconds;
    private int unloadedSeconds;

    public CChunk(CWorld cWorld, ChunkCoordinate chunkCoordinate) {
        this.cWorld = cWorld;
        this.chunkCoordinate = chunkCoordinate;
        this.loadedBlocks = new ConcurrentHashMap<>(64);
        this.queue = new PriorityQueue<>();
        this.replaceTasks = new ArrayList<>();
        this.unloadedSeconds = 0;
    }

    public CChunk(CWorld cWorld, ChunkCoordinate chunkCoordinate, int loadedSeconds, long lastLoadedTime, ConcurrentHashMap<ChunkPos, CustomCropsBlock> loadedBlocks) {
        this.cWorld = cWorld;
        this.chunkCoordinate = chunkCoordinate;
        this.loadedBlocks = loadedBlocks;
        this.lastLoadedTime = lastLoadedTime;
        this.loadedSeconds = loadedSeconds;
        this.queue = new PriorityQueue<>();
        this.replaceTasks = new ArrayList<>();
        this.unloadedSeconds = 0;
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
                tick(task.getSimpleLocation());
        }
    }

    @Override
    public long getLastLoadedTime() {
        return lastLoadedTime;
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
        for (Map.Entry<ChunkPos, CustomCropsBlock> entry : this.loadedBlocks.entrySet()) {
            switch (entry.getValue().getType()) {
//                case CROP -> this.queue.add(new CheckTask(
//                        RandomUtils.getRandomInt(0, interval),
//                        entry.getKey()
//                ));
            }
        }
    }

    private void tick(SimpleLocation location) {
        CustomCropsBlock block = loadedBlocks.get(location);
        if (block == null) return;

    }

    private void tickCrops(SimpleLocation location) {

    }

    private void tickPots(SimpleLocation location) {

    }

    private void tickSprinklers(SimpleLocation location) {

    }

    @Override
    public Optional<WorldCrop> getCropAt(SimpleLocation simpleLocation) {
        WorldCrop crop = (loadedBlocks.get(ChunkPos.getByLocation(simpleLocation)) instanceof WorldCrop worldCrop) ? worldCrop : null;
        return Optional.ofNullable(crop);
    }

    @Override
    public Optional<WorldSprinkler> getSprinklerAt(SimpleLocation simpleLocation) {
        WorldSprinkler sprinkler = (loadedBlocks.get(ChunkPos.getByLocation(simpleLocation)) instanceof WorldSprinkler worldSprinkler) ? worldSprinkler : null;
        return Optional.ofNullable(sprinkler);
    }

    @Override
    public Optional<WorldPot> getPotAt(SimpleLocation simpleLocation) {
        WorldPot pot = (loadedBlocks.get(ChunkPos.getByLocation(simpleLocation)) instanceof WorldPot worldPot) ? worldPot : null;
        return Optional.ofNullable(pot);
    }

    @Override
    public Optional<CustomCropsBlock> getBlockAt(SimpleLocation location) {
        return Optional.ofNullable(loadedBlocks.get(ChunkPos.getByLocation(location)));
    }

    @Override
    public void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount) {
        Optional<WorldSprinkler> optionalSprinkler = getSprinklerAt(location);
        if (optionalSprinkler.isEmpty()) {
            loadedBlocks.put(ChunkPos.getByLocation(location), new MemorySprinkler(sprinkler.getKey(), amount));
            CustomCropsPlugin.get().debug("When adding water to sprinkler at " + location + ", the sprinkler data doesn't exist.");
        } else {
            optionalSprinkler.get().setWater(optionalSprinkler.get().getWater() + amount);
        }
    }

    @Override
    public void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location) {
        Optional<WorldPot> optionalWorldPot = getPotAt(location);
        if (optionalWorldPot.isEmpty()) {
            MemoryPot memoryPot = new MemoryPot(pot.getKey());
            memoryPot.setFertilizer(fertilizer.getKey());
            memoryPot.setFertilizerTimes(fertilizer.getTimes());
            loadedBlocks.put(ChunkPos.getByLocation(location), memoryPot);
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
            MemoryPot memoryPot = new MemoryPot(pot.getKey());
            memoryPot.setWater(amount);
            loadedBlocks.put(ChunkPos.getByLocation(location), memoryPot);
            CustomCropsPlugin.get().getItemManager().updatePotState(location.getBukkitLocation(), pot, true, null);
            CustomCropsPlugin.get().debug("When adding water to pot at " + location + ", the pot data doesn't exist.");
        } else {
            optionalWorldPot.get().setWater(optionalWorldPot.get().getWater() + amount);
            CustomCropsPlugin.get().getItemManager().updatePotState(location.getBukkitLocation(), pot, true, optionalWorldPot.get().getFertilizer());
        }
    }

    @Override
    public void addPotAt(WorldPot pot, SimpleLocation location) {
        CustomCropsBlock previous = loadedBlocks.put(ChunkPos.getByLocation(location), pot);
        if (previous != null) {
            if (previous instanceof WorldPot) {
                CustomCropsPlugin.get().debug("Found duplicated pot data when adding pot at " + location);
            } else {
                CustomCropsPlugin.get().debug("Found unremoved data when adding crop at " + location + ". Previous type is " + previous.getType().name());
            }
        }
    }

    @Override
    public void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location) {
        CustomCropsBlock previous = loadedBlocks.put(ChunkPos.getByLocation(location), sprinkler);
        if (previous != null) {
            if (previous instanceof WorldSprinkler) {
                CustomCropsPlugin.get().debug("Found duplicated sprinkler data when adding sprinkler at " + location);
            } else {
                CustomCropsPlugin.get().debug("Found unremoved data when adding crop at " + location + ". Previous type is " + previous.getType().name());
            }
        }
    }

    @Override
    public void addCropAt(WorldCrop crop, SimpleLocation location) {
        CustomCropsBlock previous = loadedBlocks.put(ChunkPos.getByLocation(location), crop);
        if (previous != null) {
            if (previous instanceof WorldCrop) {
                CustomCropsPlugin.get().debug("Found duplicated crop data when adding crop at " + location);
            } else {
                CustomCropsPlugin.get().debug("Found unremoved data when adding crop at " + location + ". Previous type is " + previous.getType().name());
            }
        }
    }

    @Override
    public void addPointToCrop(Crop crop, SimpleLocation location, int points) {
        if (points <= 0) return;
        Optional<WorldCrop> cropData = getCropAt(location);
        int previousPoint = 0;
        if (cropData.isPresent()) {
            WorldCrop worldCrop = cropData.get();
            previousPoint = worldCrop.getPoint();
            worldCrop.setPoint(previousPoint + points);
        } else {
            loadedBlocks.put(ChunkPos.getByLocation(location), new MemoryCrop(crop.getKey(), points));
        }
        Location bkLoc = location.getBukkitLocation();
        int x = Math.min(previousPoint + points, crop.getMaxPoints());
        for (int i = previousPoint + 1; i <= x; i++) {
            Crop.Stage stage = crop.getStageByPoint(i);
            if (stage != null) {
                stage.trigger(ActionTrigger.GROW, new State(null, null, bkLoc));
            }
        }
        String pre = crop.getStageItemByPoint(previousPoint);
        String after = crop.getStageItemByPoint(x);
        if (pre.equals(after)) return;
        CustomCropsPlugin.get().getItemManager().removeAnythingAt(bkLoc);
        CustomCropsPlugin.get().getItemManager().placeItem(bkLoc, crop.getItemCarrier(), after);
    }

    @Override
    public void removeSprinklerAt(SimpleLocation location) {
        CustomCropsBlock removed = loadedBlocks.remove(ChunkPos.getByLocation(location));
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove sprinkler from " + location + " because sprinkler doesn't exist.");
        } else if (!(removed instanceof WorldSprinkler)) {
            CustomCropsPlugin.get().debug("Removed sprinkler from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public void removePotAt(SimpleLocation location) {
        CustomCropsBlock removed = loadedBlocks.remove(ChunkPos.getByLocation(location));
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove pot from " + location + " because pot doesn't exist.");
        } else if (!(removed instanceof WorldPot)) {
            CustomCropsPlugin.get().debug("Removed pot from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public void removeCropAt(SimpleLocation location) {
        CustomCropsBlock removed = loadedBlocks.remove(ChunkPos.getByLocation(location));
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove crop from " + location + " because crop doesn't exist.");
        } else if (!(removed instanceof WorldCrop)) {
            CustomCropsPlugin.get().debug("Removed crop from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public CustomCropsBlock removeAnythingAt(SimpleLocation location) {
        return loadedBlocks.remove(ChunkPos.getByLocation(location));
    }

    @Override
    public int getCropAmount() {
        int amount = 0;
        for (CustomCropsBlock block : loadedBlocks.values()) {
            if (block instanceof WorldCrop) {
                amount++;
            }
        }
        return amount;
    }

    @Override
    public int getPotAmount() {
        int amount = 0;
        for (CustomCropsBlock block : loadedBlocks.values()) {
            if (block instanceof WorldPot) {
                amount++;
            }
        }
        return amount;
    }

    @Override
    public int getSprinklerAmount() {
        int amount = 0;
        for (CustomCropsBlock block : loadedBlocks.values()) {
            if (block instanceof WorldSprinkler) {
                amount++;
            }
        }
        return amount;
    }

    public Map<ChunkPos, CustomCropsBlock> getLoadedBlocks() {
        return loadedBlocks;
    }

    public int getUnloadedSeconds() {
        return unloadedSeconds;
    }

    public void setUnloadedSeconds(int unloadedSeconds) {
        this.unloadedSeconds = unloadedSeconds;
    }
}
