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

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a region position in a Minecraft world, defined by its x and z coordinates.
 */
public record RegionPos(int x, int z) {

    /**
     * Creates a new RegionPos instance with the specified x and z coordinates.
     *
     * @param x The x-coordinate of the region.
     * @param z The z-coordinate of the region.
     * @return A new {@link RegionPos} instance.
     */
    public static RegionPos of(int x, int z) {
        return new RegionPos(x, z);
    }

    /**
     * Parses a string representation of a region position and returns a RegionPos instance.
     * The string should be in the format "x,z".
     *
     * @param coordinate The string representation of the region position.
     * @return A new {@link RegionPos} instance.
     * @throws RuntimeException if the coordinate string is invalid.
     */
    public static RegionPos getByString(String coordinate) {
        String[] split = coordinate.split(",", 2);
        try {
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[1]);
            return new RegionPos(x, z);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Invalid coordinate: " + coordinate);
        }
    }

    /**
     * Computes a hash code for this region position.
     *
     * @return A hash code representing this {@link RegionPos}.
     */
    @Override
    public int hashCode() {
        long combined = (long) x << 32 | z;
        return Long.hashCode(combined);
    }

    /**
     * Checks if this region position is equal to another object.
     *
     * @param obj The object to compare with.
     * @return true if the object is a {@link RegionPos} with the same coordinates, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegionPos other = (RegionPos) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    /**
     * Converts a Bukkit {@link Chunk} to a {@link RegionPos} by calculating the region coordinates.
     * A region covers a 32x32 area of chunks.
     *
     * @param chunk The Bukkit chunk to convert.
     * @return A new {@link RegionPos} representing the region containing the chunk.
     */
    @NotNull
    public static RegionPos getByBukkitChunk(@NotNull Chunk chunk) {
        int regionX = (int) Math.floor((double) chunk.getX() / 32.0);
        int regionZ = (int) Math.floor((double) chunk.getZ() / 32.0);
        return new RegionPos(regionX, regionZ);
    }

    /**
     * Converts a {@link ChunkPos} to a {@link RegionPos} by calculating the region coordinates.
     * A region covers a 32x32 area of chunks.
     *
     * @param chunk The {@link ChunkPos} to convert.
     * @return A new {@link RegionPos} representing the region containing the chunk position.
     */
    @NotNull
    public static RegionPos getByChunkPos(@NotNull ChunkPos chunk) {
        int regionX = (int) Math.floor((double) chunk.x() / 32.0);
        int regionZ = (int) Math.floor((double) chunk.z() / 32.0);
        return new RegionPos(regionX, regionZ);
    }

    /**
     * Returns a string representation of this RegionPos instance for debugging and logging.
     *
     * @return A string in the format "RegionPos{x=..., z=...}".
     */
    @Override
    public String toString() {
        return "RegionPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}

