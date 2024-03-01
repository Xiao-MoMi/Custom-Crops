package net.momirealms.customcrops.mechanic.world;

import com.flowpowered.nbt.CompoundTag;

import java.util.List;

public class SerializableChunk {

    private final int x;
    private final int z;
    private final int loadedSeconds;
    private final long lastLoadedTime;
    private final List<CompoundTag> blocks;
    private final List<CompoundTag> queuedTasks;

    public SerializableChunk(int x, int z, int loadedSeconds, long lastLoadedTime, List<CompoundTag> blocks, List<CompoundTag> queuedTasks) {
        this.x = x;
        this.z = z;
        this.lastLoadedTime = lastLoadedTime;
        this.loadedSeconds = loadedSeconds;
        this.blocks = blocks;
        this.queuedTasks = queuedTasks;
    }

    public int getLoadedSeconds() {
        return loadedSeconds;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long getLastLoadedTime() {
        return lastLoadedTime;
    }

    public List<CompoundTag> getBlocks() {
        return blocks;
    }

    public List<CompoundTag> getQueuedTasks() {
        return queuedTasks;
    }
}
