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
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.mechanic.world.block.MemoryPot;
import net.momirealms.customcrops.mechanic.world.block.MemorySprinkler;
import net.momirealms.customcrops.scheduler.task.TickTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CChunk implements CustomCropsChunk {

    private transient CWorld cWorld;
    private final ChunkCoordinate chunkCoordinate;
    private final ConcurrentHashMap<Integer, CSection> loadedSections;
    private final PriorityQueue<TickTask> queue;
    private final Set<ChunkPos> tickedBlocks;
    private long lastLoadedTime;
    private int loadedSeconds;
    private int unloadedSeconds;

    public CChunk(CWorld cWorld, ChunkCoordinate chunkCoordinate) {
        this.cWorld = cWorld;
        this.chunkCoordinate = chunkCoordinate;
        this.loadedSections = new ConcurrentHashMap<>(64);
        this.queue = new PriorityQueue<>();
        this.unloadedSeconds = 0;
        this.tickedBlocks = Collections.synchronizedSet(new HashSet<>());
        this.updateLastLoadedTime();
    }

    public CChunk(
            CWorld cWorld,
            ChunkCoordinate chunkCoordinate,
            int loadedSeconds,
            long lastLoadedTime,
            ConcurrentHashMap<Integer, CSection> loadedSections,
            PriorityQueue<TickTask> queue,
            HashSet<ChunkPos> tickedBlocks
    ) {
        this.cWorld = cWorld;
        this.chunkCoordinate = chunkCoordinate;
        this.loadedSections = loadedSections;
        this.lastLoadedTime = lastLoadedTime;
        this.loadedSeconds = loadedSeconds;
        this.queue = queue;
        this.unloadedSeconds = 0;
        this.tickedBlocks = Collections.synchronizedSet(tickedBlocks);
    }

    @Override
    public void updateLastLoadedTime() {
        this.lastLoadedTime = System.currentTimeMillis();
    }

    @Override
    public void notifyOfflineUpdates() {
        long delta = this.lastLoadedTime - System.currentTimeMillis();
        int seconds = (int) (delta / 1000);

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
        WorldSetting setting = cWorld.getWorldSetting();
        int interval = setting.getMinTickUnit();
        this.loadedSeconds++;
        // if loadedSeconds reach another recycle, rearrange the tasks
        if (this.loadedSeconds >= interval) {
            this.loadedSeconds = 0;
            this.tickedBlocks.clear();
            this.queue.clear();
            if (setting.isScheduledTick()) {
                this.arrangeTasks(setting.getMinTickUnit());
            }
        }

        // scheduled tick
        while (!queue.isEmpty() && queue.peek().getTime() <= loadedSeconds) {
            TickTask task = queue.poll();
            if (task != null) {
                ChunkPos pos = task.getChunkPos();
                CSection section = loadedSections.get(pos.getSectionID());
                if (section != null) {
                    CustomCropsBlock block = section.getBlockAt(pos);
                    if (block == null) continue;
                    switch (block.getType()) {
                        case SCARECROW, GREENHOUSE -> {}
                        case POT -> {
                            if (!setting.randomTickPot()) {
                                block.tick(setting.getTickPotInterval());
                            }
                        }
                        case CROP -> {
                            if (!setting.randomTickCrop()) {
                                block.tick(setting.getTickCropInterval());
                            }
                        }
                        case SPRINKLER -> {
                            if (!setting.randomTickSprinkler()) {
                                block.tick(setting.getTickSprinklerInterval());
                            }
                        }
                    }
                }
            }
        }

        // random tick
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomTicks = setting.getRandomTickSpeed();
        for (CustomCropsSection section : getSections()) {
            int sectionID = section.getSectionID();
            int baseY = sectionID * 16;
            for (int i = 0; i < randomTicks; i++) {
                int x = random.nextInt(16);
                int y = random.nextInt(16) + baseY;
                int z = random.nextInt(16);
                CustomCropsBlock block = section.getBlockAt(new ChunkPos(x,y,z));
                if (block != null) {
                    switch (block.getType()) {
                        case CROP -> {
                            if (setting.randomTickCrop()) {
                                block.tick(setting.getTickCropInterval());
                            }
                        }
                        case SPRINKLER -> {
                            if (setting.randomTickSprinkler()) {
                                block.tick(setting.getTickSprinklerInterval());
                            }
                        }
                        case POT -> {
                            ((WorldPot) block).tickWater(this);
                            if (setting.randomTickPot()) {
                                block.tick(setting.getTickPotInterval());
                            }
                        }
                    }
                }
            }
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

    public void arrangeTasks(int unit) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (CustomCropsSection section : getSections()) {
            for (Map.Entry<ChunkPos, CustomCropsBlock> entry : section.getBlockMap().entrySet()) {
                this.queue.add(new TickTask(
                        random.nextInt(0, unit),
                        entry.getKey()
                ));
                this.tickedBlocks.add(entry.getKey());
            }
        }
    }

    public void tryCreatingTaskForNewBlock(ChunkPos pos) {
        WorldSetting setting = cWorld.getWorldSetting();
        if (setting.isScheduledTick() && !tickedBlocks.contains(pos)) {
            tickedBlocks.add(pos);
            int random = ThreadLocalRandom.current().nextInt(0, setting.getMinTickUnit());
            if (random > loadedSeconds) {
                queue.add(new TickTask(random, pos));
            }
        }
    }

    @Override
    public Optional<WorldCrop> getCropAt(SimpleLocation simpleLocation) {
        return getBlockAt(simpleLocation).map(customCropsBlock -> customCropsBlock instanceof WorldCrop worldCrop ? worldCrop : null);
    }

    @Override
    public Optional<WorldSprinkler> getSprinklerAt(SimpleLocation simpleLocation) {
        return getBlockAt(simpleLocation).map(customCropsBlock -> customCropsBlock instanceof WorldSprinkler worldSprinkler ? worldSprinkler : null);
    }

    @Override
    public Optional<WorldPot> getPotAt(SimpleLocation simpleLocation) {
        return getBlockAt(simpleLocation).map(customCropsBlock -> customCropsBlock instanceof WorldPot worldPot ? worldPot : null);
    }

    @Override
    public Optional<WorldGlass> getGlassAt(SimpleLocation simpleLocation) {
        return getBlockAt(simpleLocation).map(customCropsBlock -> customCropsBlock instanceof WorldGlass worldGlass ? worldGlass : null);
    }

    @Override
    public Optional<WorldScarecrow> getScarecrowAt(SimpleLocation simpleLocation) {
        return getBlockAt(simpleLocation).map(customCropsBlock -> customCropsBlock instanceof WorldScarecrow worldScarecrow ? worldScarecrow : null);
    }

    @Override
    public void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount) {
        Optional<WorldSprinkler> optionalSprinkler = getSprinklerAt(location);
        if (optionalSprinkler.isEmpty()) {
            addBlockAt(new MemorySprinkler(location, sprinkler.getKey(), amount), location);
            CustomCropsPlugin.get().debug("When adding water to sprinkler at " + location + ", the sprinkler data doesn't exist.");
            if (sprinkler.get3DItemWithWater() != null) {
                CustomCropsPlugin.get().getItemManager().removeAnythingAt(location.getBukkitLocation());
                CustomCropsPlugin.get().getItemManager().placeItem(location.getBukkitLocation(), sprinkler.getItemCarrier(), sprinkler.get3DItemWithWater());
            }
        } else {
            int current = optionalSprinkler.get().getWater();
            if (current == 0) {
                if (sprinkler.get3DItemWithWater() != null) {
                    CustomCropsPlugin.get().getItemManager().removeAnythingAt(location.getBukkitLocation());
                    CustomCropsPlugin.get().getItemManager().placeItem(location.getBukkitLocation(), sprinkler.getItemCarrier(), sprinkler.get3DItemWithWater());
                }
            }
            optionalSprinkler.get().setWater(current + amount);
        }
    }

    @Override
    public void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location) {
        Optional<WorldPot> optionalWorldPot = getPotAt(location);
        if (optionalWorldPot.isEmpty()) {
            MemoryPot memoryPot = new MemoryPot(location, pot.getKey());
            memoryPot.setFertilizer(fertilizer);
            addBlockAt(memoryPot, location);
            CustomCropsPlugin.get().debug("When adding fertilizer to pot at " + location + ", the pot data doesn't exist.");
        } else {
            optionalWorldPot.get().setFertilizer(fertilizer);
        }
    }

    @Override
    public void addWaterToPot(Pot pot, SimpleLocation location, int amount) {
        Optional<WorldPot> optionalWorldPot = getPotAt(location);
        if (optionalWorldPot.isEmpty()) {
            MemoryPot memoryPot = new MemoryPot(location, pot.getKey());
            memoryPot.setWater(amount);
            addBlockAt(memoryPot, location);
            CustomCropsPlugin.get().getItemManager().updatePotState(location.getBukkitLocation(), pot, true, null);
            CustomCropsPlugin.get().debug("When adding water to pot at " + location + ", the pot data doesn't exist.");
        } else {
            optionalWorldPot.get().setWater(optionalWorldPot.get().getWater() + amount);
            CustomCropsPlugin.get().getItemManager().updatePotState(location.getBukkitLocation(), pot, true, optionalWorldPot.get().getFertilizer());
        }
    }

    @Override
    public void addPotAt(WorldPot pot, SimpleLocation location) {
        CustomCropsBlock previous = addBlockAt(pot, location);
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
        CustomCropsBlock previous = addBlockAt(sprinkler, location);
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
        CustomCropsBlock previous = addBlockAt(crop, location);
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
        if (cropData.isEmpty()) {
            return;
        }
        WorldCrop worldCrop = cropData.get();
        int previousPoint = worldCrop.getPoint();
        int x = Math.min(previousPoint + points, crop.getMaxPoints());
        worldCrop.setPoint(x);
        Location bkLoc = location.getBukkitLocation();
        if (bkLoc == null) return;
        for (int i = previousPoint + 1; i <= x; i++) {
            Crop.Stage stage = crop.getStageByPoint(i);
            if (stage != null) {
                stage.trigger(ActionTrigger.GROW, new State(null, new ItemStack(Material.AIR), bkLoc));
            }
        }
        String pre = crop.getStageItemByPoint(previousPoint);
        String after = crop.getStageItemByPoint(x);
        if (pre.equals(after)) return;
        CustomCropsPlugin.get().getItemManager().removeAnythingAt(bkLoc);
        CustomCropsPlugin.get().getItemManager().placeItem(bkLoc, crop.getItemCarrier(), after);
    }

    @Override
    public void addGlassAt(WorldGlass glass, SimpleLocation location) {
        CustomCropsBlock previous = addBlockAt(glass, location);
        if (previous != null) {
            if (previous instanceof WorldGlass) {
                CustomCropsPlugin.get().debug("Found duplicated glass data when adding crop at " + location);
            } else {
                CustomCropsPlugin.get().debug("Found unremoved data when adding glass at " + location + ". Previous type is " + previous.getType().name());
            }
        }
    }

    @Override
    public void addScarecrowAt(WorldScarecrow scarecrow, SimpleLocation location) {
        CustomCropsBlock previous = addBlockAt(scarecrow, location);
        if (previous != null) {
            if (previous instanceof WorldScarecrow) {
                CustomCropsPlugin.get().debug("Found duplicated glass data when adding scarecrow at " + location);
            } else {
                CustomCropsPlugin.get().debug("Found unremoved data when adding scarecrow at " + location + ". Previous type is " + previous.getType().name());
            }
        }
    }

    @Override
    public void removeSprinklerAt(SimpleLocation location) {
        CustomCropsBlock removed = removeBlockAt(location);
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove sprinkler from " + location + " because sprinkler doesn't exist.");
        } else if (!(removed instanceof WorldSprinkler)) {
            CustomCropsPlugin.get().debug("Removed sprinkler from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public void removePotAt(SimpleLocation location) {
        CustomCropsBlock removed = removeBlockAt(location);
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove pot from " + location + " because pot doesn't exist.");
        } else if (!(removed instanceof WorldPot)) {
            CustomCropsPlugin.get().debug("Removed pot from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public void removeCropAt(SimpleLocation location) {
        CustomCropsBlock removed = removeBlockAt(location);
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove crop from " + location + " because crop doesn't exist.");
        } else if (!(removed instanceof WorldCrop)) {
            CustomCropsPlugin.get().debug("Removed crop from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public void removeGlassAt(SimpleLocation location) {
        CustomCropsBlock removed = removeBlockAt(location);
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove glass from " + location + " because glass doesn't exist.");
        } else if (!(removed instanceof WorldGlass)) {
            CustomCropsPlugin.get().debug("Removed glass from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public void removeScarecrowAt(SimpleLocation location) {
        CustomCropsBlock removed = removeBlockAt(location);
        if (removed == null) {
            CustomCropsPlugin.get().debug("Failed to remove scarecrow from " + location + " because scarecrow doesn't exist.");
        } else if (!(removed instanceof WorldScarecrow)) {
            CustomCropsPlugin.get().debug("Removed scarecrow from " + location + " but the previous block type is " + removed.getType().name());
        }
    }

    @Override
    public CustomCropsBlock removeBlockAt(SimpleLocation location) {
        ChunkPos pos = ChunkPos.getByLocation(location);
        CSection section = loadedSections.get(pos.getSectionID());
        if (section == null) return null;
        return section.removeBlockAt(pos);
    }

    @Override
    public CustomCropsBlock addBlockAt(CustomCropsBlock block, SimpleLocation location) {
        ChunkPos pos = ChunkPos.getByLocation(location);
        CSection section = loadedSections.get(pos.getSectionID());
        if (section == null) {
            section = new CSection(pos.getSectionID());
            loadedSections.put(pos.getSectionID(), section);
        }
        this.tryCreatingTaskForNewBlock(pos);
        return section.addBlockAt(pos, block);
    }

    @Override
    public Optional<CustomCropsBlock> getBlockAt(SimpleLocation location) {
        ChunkPos pos = ChunkPos.getByLocation(location);
        CSection section = loadedSections.get(pos.getSectionID());
        if (section == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(section.getBlockAt(pos));
    }

    @Override
    public int getCropAmount() {
        int amount = 0;
        for (CustomCropsSection section : getSections()) {
            for (CustomCropsBlock block : section.getBlocks()) {
                if (block instanceof WorldCrop) {
                    amount++;
                }
            }
        }
        return amount;
    }

    @Override
    public int getPotAmount() {
        int amount = 0;
        for (CustomCropsSection section : getSections()) {
            for (CustomCropsBlock block : section.getBlocks()) {
                if (block instanceof WorldPot) {
                    amount++;
                }
            }
        }
        return amount;
    }

    @Override
    public int getSprinklerAmount() {
        int amount = 0;
        for (CustomCropsSection section : getSections()) {
            for (CustomCropsBlock block : section.getBlocks()) {
                if (block instanceof WorldSprinkler) {
                    amount++;
                }
            }
        }
        return amount;
    }

    public CSection[] getSectionsForSerialization() {
        ArrayList<CSection> sections = new ArrayList<>();
        for (Map.Entry<Integer, CSection> entry : loadedSections.entrySet()) {
            if (!entry.getValue().canPrune()) {
                sections.add(entry.getValue());
            }
        }
        return sections.toArray(new CSection[0]);
    }

    @Override
    public CustomCropsSection[] getSections() {
        return loadedSections.values().toArray(new CustomCropsSection[0]);
    }

    @Override
    public CustomCropsSection getSection(int sectionID) {
        return loadedSections.get(sectionID);
    }

    @Override
    public int getUnloadedSeconds() {
        return unloadedSeconds;
    }

    @Override
    public void setUnloadedSeconds(int unloadedSeconds) {
        this.unloadedSeconds = unloadedSeconds;
    }

    @Override
    public boolean canPrune() {
        return loadedSections.size() == 0;
    }

    public PriorityQueue<TickTask> getQueue() {
        return queue;
    }

    public Set<ChunkPos> getTickedBlocks() {
        return tickedBlocks;
    }
}
