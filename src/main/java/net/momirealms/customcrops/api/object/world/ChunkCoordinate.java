package net.momirealms.customcrops.api.object.world;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class ChunkCoordinate implements Serializable {

    private final int x;
    private final int z;

    public ChunkCoordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getFileName() {
        return x + "," + z;
    }

    @Override
    public int hashCode() {
        long combined = (long) x << 32 | z;
        return Long.hashCode(combined);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChunkCoordinate other = (ChunkCoordinate) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    @Nullable
    public static ChunkCoordinate getByString(@NotNull String str) {
        String[] split = str.split(",", 2);
        try {
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[1]);
            return new ChunkCoordinate(x, z);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static ChunkCoordinate getByBukkitChunk(@NotNull Chunk chunk) {
        return new ChunkCoordinate(chunk.getX(), chunk.getZ());
    }
}
