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

/**
 * Represents a position within a chunk in the world. The position is encoded into a single integer for compact storage.
 * The position is constructed using the x, y, and z coordinates, with the x and z limited to the range [0, 15]
 * (as they represent positions within a 16x16 chunk), and y representing the height.
 */
public class BlockPos {

    private final int position;

    /**
     * Constructs a BlockPos with an already encoded position.
     *
     * @param position The encoded position.
     */
    public BlockPos(int position) {
        this.position = position;
    }

    /**
     * Constructs a BlockPos from x, y, and z coordinates within a chunk.
     *
     * @param x The x-coordinate within the chunk (0-15).
     * @param y The y-coordinate (world height).
     * @param z The z-coordinate within the chunk (0-15).
     */
    public BlockPos(int x, int y, int z) {
        this.position = ((x & 0xF) << 28) | ((z & 0xF) << 24) | (y & 0xFFFFFF);
    }

    /**
     * Creates a BlockPos from a Pos3 object, adjusting x and z to be within the chunk.
     *
     * @param location The Pos3 object representing a 3D coordinate.
     * @return A new BlockPos object.
     */
    public static BlockPos fromPos3(Pos3 location) {
        return new BlockPos(location.x() % 16, location.y(), location.z() % 16);
    }

    /**
     * Converts this BlockPos into a Pos3 object, calculating absolute world coordinates using the provided ChunkPos.
     *
     * @param coordinate The ChunkPos representing the chunk coordinates.
     * @return A Pos3 object representing the world coordinates.
     */
    public Pos3 toPos3(ChunkPos coordinate) {
        return new Pos3(coordinate.x() * 16 + x(), y(), coordinate.z() * 16 + z());
    }

    /**
     * Retrieves the encoded position value.
     *
     * @return The encoded position.
     */
    public int position() {
        return position;
    }

    /**
     * Retrieves the x-coordinate within the chunk.
     *
     * @return The x-coordinate (0-15).
     */
    public int x() {
        return (position >> 28) & 0xF;
    }

    /**
     * Retrieves the z-coordinate within the chunk.
     *
     * @return The z-coordinate (0-15).
     */
    public int z() {
        return (position >> 24) & 0xF;
    }

    /**
     * Retrieves the y-coordinate (world height).
     *
     * @return The y-coordinate.
     */
    public int y() {
        int y = position & 0xFFFFFF;
        if ((y & 0x800000) != 0) {  // Check if y is negative in 24-bit signed integer representation
            y |= 0xFF000000;  // Extend the sign bit to make it a 32-bit signed integer.
        }
        return y;
    }

    /**
     * Calculates the section ID based on the y-coordinate.
     *
     * @return The section ID, representing which 16-block high section of the world the position is in.
     */
    public int sectionID() {
        return (int) Math.floor((double) y() / 16);
    }

    /**
     * Checks if this BlockPos is equal to another object.
     *
     * @param o The object to compare.
     * @return true if the object is a BlockPos with the same encoded position, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos blockPos = (BlockPos) o;
        return position == blockPos.position;
    }

    /**
     * Computes a hash code for this BlockPos.
     *
     * @return A hash code derived from the encoded position.
     */
    @Override
    public int hashCode() {
        return Math.abs(position);
    }

    /**
     * Returns a string representation of this BlockPos.
     *
     * @return A string in the format "BlockPos{x=..., y=..., z=...}".
     */
    @Override
    public String toString() {
        return "BlockPos{" +
                "x=" + x() +
                "y=" + y() +
                "z=" + z() +
                '}';
    }
}
