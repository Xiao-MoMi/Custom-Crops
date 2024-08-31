package net.momirealms.customcrops.api.core.world;

import net.momirealms.customcrops.common.util.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class CustomCropsChunkImpl implements CustomCropsChunk {

    private final CustomCropsWorld<?> world;
    private final ChunkPos chunkPos;
    private final ConcurrentHashMap<Integer, CustomCropsSection> loadedSections;
    private final PriorityQueue<DelayedTickTask> queue;
    private final Set<BlockPos> tickedBlocks;
    private long lastLoadedTime;
    private int loadedSeconds;
    private int unloadedSeconds;
    private boolean notified;
    private boolean isLoaded;
    private boolean forceLoad;

    protected CustomCropsChunkImpl(CustomCropsWorld<?> world, ChunkPos chunkPos) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.loadedSections = new ConcurrentHashMap<>(16);
        this.queue = new PriorityQueue<>();
        this.unloadedSeconds = 0;
        this.tickedBlocks = Collections.synchronizedSet(new HashSet<>());
        this.updateLastLoadedTime();
        this.notified = true;
        this.isLoaded = false;
    }

    protected CustomCropsChunkImpl(
            CustomCropsWorld<?> world,
            ChunkPos chunkPos,
            int loadedSeconds,
            long lastLoadedTime,
            ConcurrentHashMap<Integer, CustomCropsSection> loadedSections,
            PriorityQueue<DelayedTickTask> queue,
            HashSet<BlockPos> tickedBlocks
    ) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.loadedSections = loadedSections;
        this.lastLoadedTime = lastLoadedTime;
        this.loadedSeconds = loadedSeconds;
        this.queue = queue;
        this.unloadedSeconds = 0;
        this.tickedBlocks = Collections.synchronizedSet(tickedBlocks);
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
        this.loadedSeconds++;
        // if loadedSeconds reach another recycle, rearrange the tasks
        if (this.loadedSeconds >= interval) {
            this.loadedSeconds = 0;
            this.tickedBlocks.clear();
            this.queue.clear();
            this.arrangeTasks(interval);
        }
        scheduledTick();
        randomTick(setting.randomTickSpeed());
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

    private void scheduledTick() {
        while (!queue.isEmpty() && queue.peek().getTime() <= loadedSeconds) {
            DelayedTickTask task = queue.poll();
            if (task != null) {
                BlockPos pos = task.blockPos();
                CustomCropsSection section = loadedSections.get(pos.sectionID());
                if (section != null) {
                    Optional<CustomCropsBlockState> block = section.getBlockState(pos);
                    block.ifPresent(state -> state.type().scheduledTick(state, world, pos.toPos3(chunkPos)));
                }
            }
        }
    }

    private void randomTick(int randomTickSpeed) {
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
                block.ifPresent(state -> state.type().randomTick(state, world, pos.toPos3(chunkPos)));
            }
        }
    }

    @Override
    public int unloadedSeconds() {
        return unloadedSeconds;
    }

    @Override
    public void unloadedSeconds(int unloadedSeconds) {
        this.unloadedSeconds = unloadedSeconds;
    }

    @Override
    public long lastLoadedTime() {
        return lastLoadedTime;
    }

    @Override
    public void updateLastLoadedTime() {
        this.lastLoadedTime = System.currentTimeMillis();
    }

    @Override
    public int loadedMilliSeconds() {
        return (int) (System.currentTimeMillis() - lastLoadedTime);
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
    public Collection<CustomCropsSection> sections() {
        return loadedSections.values();
    }

    @Override
    public Optional<CustomCropsSection> removeSection(int sectionID) {
        return Optional.ofNullable(loadedSections.remove(sectionID));
    }

    @Override
    public void resetUnloadedSeconds() {
        this.unloadedSeconds = 0;
        this.notified = false;
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
    public PriorityQueue<DelayedTickTask> tickTaskQueue() {
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
