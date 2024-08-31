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

package net.momirealms.customcrops.api.core.world;

public class BlockPos {

    private final int position;

    public BlockPos(int position) {
        this.position = position;
    }

    public BlockPos(int x, int y, int z) {
        this.position = ((x & 0xF) << 28) | ((z & 0xF) << 24) | (y & 0xFFFFFF);
    }

    public static BlockPos fromPos3(Pos3 location) {
        return new BlockPos(location.x() % 16, location.y(), location.z() % 16);
    }

    public Pos3 toPos3(ChunkPos coordinate) {
        return new Pos3(coordinate.x() * 16 + x(), y(), coordinate.z() * 16 + z());
    }

    public int position() {
        return position;
    }

    public int x() {
        return (position >> 28) & 0xF;
    }

    public int z() {
        return (position >> 24) & 0xF;
    }

    public int y() {
        int y = position & 0xFFFFFF;
        if ((y & 0x800000) != 0) {
            y |= 0xFF000000;
        }
        return y;
    }

    public int sectionID() {
        return (int) Math.floor((double) y() / 16);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos blockPos = (BlockPos) o;
        return position == blockPos.position;
    }

    @Override
    public int hashCode() {
        return Math.abs(position);
    }

    @Override
    public String toString() {
        return "BlockPos{" +
                "x=" + x() +
                "y=" + y() +
                "z=" + z() +
                '}';
    }
}
