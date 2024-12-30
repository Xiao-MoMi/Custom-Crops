/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.world;

import net.momirealms.customcrops.common.util.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class CustomCropsChunkImpl implements CustomCropsChunk {

    private final CustomCropsWorld<?> world;
    private final ChunkPos chunkPos;
    private final ConcurrentHashMap<Integer, CustomCropsSection> loadedSections;
    private final PriorityBlockingQueue<DelayedTickTask> queue;
    private final Set<BlockPos> tickedBlocks;
    private long lastUnloadTime;
    private int loadedSeconds;
    private int lazySeconds;
    private boolean notified;
    private boolean isLoaded;
    private boolean forceLoad;

    // new chunk
    protected CustomCropsChunkImpl(CustomCropsWorld<?> world, ChunkPos chunkPos) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.loadedSections = new ConcurrentHashMap<>(16);
        this.queue = new PriorityBlockingQueue<>();
        this.lazySeconds = 0;
        this.tickedBlocks = Collections.synchronizedSet(new HashSet<>());
        this.notified = true;
        this.isLoaded = false;
        this.updateLastUnloadTime();
    }

    protected CustomCropsChunkImpl(
            CustomCropsWorld<?> world,
            ChunkPos chunkPos,
            int loadedSeconds,
            long lastUnloadTime,
            ConcurrentHashMap<Integer, CustomCropsSection> loadedSections,
            PriorityBlockingQueue<DelayedTickTask> queue,
            HashSet<BlockPos> tickedBlocks
    ) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.loadedSections = loadedSections;
        this.lastUnloadTime = lastUnloadTime;
        this.loadedSeconds = loadedSeconds;
        this.queue = queue;
        this.lazySeconds = 0;
        this.tickedBlocks = Collections.synchronizedSet(tickedBlocks);
        this.notified = false;
        this.isLoaded = false;
    }

    @Override
    public void setForceLoaded(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }

    @Override
    public boolean isForceLoaded() {
        return this.forceLoad;
    }

    @Override
    public void load(boolean loadBukkitChunk) {
        if (!isLoaded()) {
            if (((CustomCropsWorldImpl<?>) world).loadChunk(this)) {
                this.isLoaded = true;
                this.lazySeconds = 0;
            }
            if (loadBukkitChunk && !this.world.bukkitWorld().isChunkLoaded(chunkPos.x(), chunkPos.z())) {
                this.world.bukkitWorld().getChunkAt(chunkPos.x(), chunkPos.z());
            }
        }
    }

    @Override
    public void unload(boolean lazy) {
        if (isLoaded() && !isForceLoaded()) {
            if (((CustomCropsWorldImpl<?>) world).unloadChunk(this, lazy)) {
                this.isLoaded = false;
                this.notified = false;
                this.lazySeconds = 0;
            }
        }
    }

    @Override
    public void unloadLazy() {
        if (!isLoaded() && isLazy()) {
           ((CustomCropsWorldImpl<?>) world).unloadLazyChunk(chunkPos);
        }
    }

    @Override
    public boolean isLazy() {
        return ((CustomCropsWorldImpl<?>) world).getLazyChunk(chunkPos) == this;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public CustomCropsWorld<?> getWorld() {
        return world;
    }

    @Override
    public ChunkPos chunkPos() {
        return chunkPos;
    }

    @Override
    public void timer() {
        WorldSetting setting = world.setting();
        int interval = setting.minTickUnit();
        if (this.loadedSeconds < 0) this.loadedSeconds = 0;
        this.loadedSeconds++;
        // if loadedSeconds reach another recycle, rearrange the tasks
        if (this.loadedSeconds >= interval) {
            this.loadedSeconds = 0;
            this.tickedBlocks.clear();
            this.queue.clear();
            this.arrangeTasks(interval);
        }
        scheduledTick(false);
        randomTick(setting.randomTickSpeed(), false);
    }

    private void arrangeTasks(int unit) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (CustomCropsSection section : loadedSections.values()) {
            for (Map.Entry<BlockPos, CustomCropsBlockState> entry : section.blockMap().entrySet()) {
                this.queue.add(new DelayedTickTask(
                        random.nextInt(0, unit),
                        entry.getKey()
                ));
                this.tickedBlocks.add(entry.getKey());
            }
        }
    }

    private void scheduledTick(boolean offline) {
        while (!queue.isEmpty() && queue.peek().getTime() <= loadedSeconds) {
            DelayedTickTask task = queue.poll();
            if (task != null) {
                BlockPos pos = task.blockPos();
                CustomCropsSection section = loadedSections.get(pos.sectionID());
                if (section != null) {
                    Optional<CustomCropsBlockState> block = section.getBlockState(pos);
                    block.ifPresent(state -> state.type().scheduledTick(state, world, pos.toPos3(chunkPos), offline));
                }
            }
        }
    }

    private void randomTick(int randomTickSpeed, boolean offline) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (CustomCropsSection section : loadedSections.values()) {
            int sectionID = section.getSectionID();
            int baseY = sectionID * 16;
            for (int i = 0; i < randomTickSpeed; i++) {
                int x = random.nextInt(16);
                int y = random.nextInt(16) + baseY;
                int z = random.nextInt(16);
                BlockPos pos = new BlockPos(x,y,z);
                Optional<CustomCropsBlockState> block = section.getBlockState(pos);
                block.ifPresent(state -> state.type().randomTick(state, world, pos.toPos3(chunkPos), offline));
            }
        }
    }

    @Override
    public int lazySeconds() {
        return lazySeconds;
    }

    @Override
    public void lazySeconds(int lazySeconds) {
        this.lazySeconds = lazySeconds;
    }

    @Override
    public long lastLoadedTime() {
        return lastUnloadTime;
    }

    @Override
    public void updateLastUnloadTime() {
        this.lastUnloadTime = System.currentTimeMillis();
    }

    @Override
    public int loadedMilliSeconds() {
        return (int) (System.currentTimeMillis() - lastUnloadTime);
    }

    @Override
    public int loadedSeconds() {
        return loadedSeconds;
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> getBlockState(Pos3 location) {
        BlockPos pos = BlockPos.fromPos3(location);
        return getLoadedSection(pos.sectionID()).flatMap(section -> section.getBlockState(pos));
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> removeBlockState(Pos3 location) {
        BlockPos pos = BlockPos.fromPos3(location);
        return getLoadedSection(pos.sectionID()).flatMap(section -> section.removeBlockState(pos));
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> addBlockState(Pos3 location, CustomCropsBlockState block) {
        BlockPos pos = BlockPos.fromPos3(location);
        CustomCropsSection section = getSection(pos.sectionID());
        this.arrangeScheduledTickTaskForNewBlock(pos);
        return section.addBlockState(pos, block);
    }

    @NotNull
    @Override
    public Stream<CustomCropsSection> sectionsToSave() {
        return loadedSections.values().stream().filter(section -> !section.canPrune());
    }

    @NotNull
    @Override
    public Optional<CustomCropsSection> getLoadedSection(int sectionID) {
        return Optional.ofNullable(loadedSections.get(sectionID));
    }

    @Override
    public CustomCropsSection getSection(int sectionID) {
        return getLoadedSection(sectionID).orElseGet(() -> {
           CustomCropsSection section = new CustomCropsSectionImpl(sectionID);
           this.loadedSections.put(sectionID, section);
           return section;
        });
    }

    @Override
    public CustomCropsSection[] sections() {
        return loadedSections.values().toArray(new CustomCropsSection[0]);
    }

    @Override
    public Optional<CustomCropsSection> removeSection(int sectionID) {
        return Optional.ofNullable(loadedSections.remove(sectionID));
    }

    @Override
    public boolean canPrune() {
        return loadedSections.isEmpty();
    }

    @Override
    public boolean isOfflineTaskNotified() {
        return notified;
    }

    @Override
    public void notifyOfflineTask() {
        if (isOfflineTaskNotified()) return;
        this.notified = true;
        long current = System.currentTimeMillis();
        int offlineTimeInSeconds = (int) (current - lastLoadedTime()) / 1000;
        WorldSetting setting = world.setting();
        offlineTimeInSeconds = Math.min(offlineTimeInSeconds, setting.maxOfflineTime());
        int minTickUnit = setting.minTickUnit();
        int randomTickSpeed = setting.randomTickSpeed();
        int threshold = setting.maxLoadingTime();
        int i = 0;
        long time1 = System.currentTimeMillis();
        while (i < offlineTimeInSeconds) {
            this.loadedSeconds++;
            if (this.loadedSeconds >= minTickUnit) {
                this.loadedSeconds = 0;
                this.tickedBlocks.clear();
                this.queue.clear();
                this.arrangeTasks(minTickUnit);
            }
            scheduledTick(true);
            randomTick(randomTickSpeed, true);
            i++;
            if (System.currentTimeMillis() - time1 > threshold) {
                break;
            }
        }
    }

    @Override
    public PriorityBlockingQueue<DelayedTickTask> tickTaskQueue() {
        return queue;
    }

    @Override
    public Set<BlockPos> tickedBlocks() {
        return tickedBlocks;
    }

    private void arrangeScheduledTickTaskForNewBlock(BlockPos pos) {
        WorldSetting setting = world.setting();
        if (!tickedBlocks.contains(pos)) {
            tickedBlocks.add(pos);
            int random = RandomUtils.generateRandomInt(0, setting.minTickUnit() - 1);
            if (random > loadedSeconds) {
                queue.add(new DelayedTickTask(random, pos));
            }
        }
    }
}
