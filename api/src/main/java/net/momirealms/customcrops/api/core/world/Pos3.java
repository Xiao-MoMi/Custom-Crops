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

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a 3-dimensional position (x, y, z) in a Minecraft world.
 */
public record Pos3(int x, int y, int z) {

    /**
     * Checks if this position is equal to another object.
     *
     * @param obj The object to compare with.
     * @return true if the object is a Pos3 with the same coordinates, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pos3 other = (Pos3) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        return true;
    }

    /**
     * Computes a hash code for this position.
     *
     * @return A hash code representing this Pos3.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }

    /**
     * Converts a Bukkit {@link Location} to a Pos3 instance.
     *
     * @param location The Bukkit location to convert.
     * @return A new Pos3 instance representing the block coordinates of the location.
     */
    public static Pos3 from(Location location) {
        return new Pos3(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Converts this Pos3 instance to a Bukkit {@link Location}.
     *
     * @param world The Bukkit world to associate with the location.
     * @return A new Location instance with this Pos3's coordinates in the specified world.
     */
    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    /**
     * Adds the specified values to this position's coordinates and returns a new Pos3 instance.
     *
     * @param x The amount to add to the x-coordinate.
     * @param y The amount to add to the y-coordinate.
     * @param z The amount to add to the z-coordinate.
     * @return A new Pos3 instance with updated coordinates.
     */
    public Pos3 add(int x, int y, int z) {
        return new Pos3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Converts this Pos3 instance to a {@link ChunkPos}, representing the chunk coordinates of this position.
     *
     * @return The {@link ChunkPos} containing this Pos3.
     */
    public ChunkPos toChunkPos() {
        return ChunkPos.fromPos3(this);
    }

    /**
     * Calculates the chunk x-coordinate of this position.
     *
     * @return The chunk x-coordinate.
     */
    public int chunkX() {
        return (int) Math.floor((double) this.x() / 16.0);
    }

    /**
     * Calculates the chunk z-coordinate of this position.
     *
     * @return The chunk z-coordinate.
     */
    public int chunkZ() {
        return (int) Math.floor((double) this.z() / 16.0);
    }

    /**
     * Returns a string representation of this Pos3 instance for debugging and logging.
     *
     * @return A string in the format "Pos3{x=..., y=..., z=...}".
     */
    @Override
    public String toString() {
        return "Pos3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
