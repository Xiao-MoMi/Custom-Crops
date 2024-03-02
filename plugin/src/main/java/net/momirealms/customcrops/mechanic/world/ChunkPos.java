package net.momirealms.customcrops.mechanic.world;

import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;

import java.util.Objects;

public class ChunkPos {

    private final int position;

    public ChunkPos(int position) {
        this.position = position;
    }

    public ChunkPos(int x, int y, int z) {
        this.position = ((x & 0xF) << 28) | ((z & 0xF) << 24) | (y & 0xFFFFFF);
    }

    public static ChunkPos getByLocation(SimpleLocation location) {
        return new ChunkPos(location.getX() % 16, location.getY(), location.getZ() % 16);
    }

    public int getPosition() {
        return position;
    }

    public int getX() {
        return (position >> 28) & 0xF;
    }

    public int getZ() {
        return (position >> 24) & 0xF;
    }

    public int getY() {
        int y = position & 0xFFFFFF;
        if ((y & 0x800000) != 0) {
            y |= 0xFF000000;
        }
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return position == chunkPos.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
