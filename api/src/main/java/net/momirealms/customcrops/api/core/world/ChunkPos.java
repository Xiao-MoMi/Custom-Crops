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

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public record ChunkPos(int x, int z) {

    public static ChunkPos of(int x, int z) {
        return new ChunkPos(x, z);
    }

    public static ChunkPos fromString(String coordinate) {
        String[] split = coordinate.split(",", 2);
        try {
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[1]);
            return new ChunkPos(x, z);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChunkPos fromPos3(Pos3 pos3) {
        int chunkX = (int) Math.floor((double) pos3.x() / 16.0);
        int chunkZ = (int) Math.floor((double) pos3.z() / 16.0);
        return ChunkPos.of(chunkX, chunkZ);
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
        final ChunkPos other = (ChunkPos) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    @NotNull
    public RegionPos toRegionPos() {
        return RegionPos.getByChunkPos(this);
    }

    @NotNull
    public static ChunkPos fromBukkitChunk(@NotNull Chunk chunk) {
        return new ChunkPos(chunk.getX(), chunk.getZ());
    }

    public String asString() {
        return x + "," + z;
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
