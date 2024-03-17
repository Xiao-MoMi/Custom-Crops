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

package net.momirealms.customcrops.api.mechanic.world;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public record RegionPos(int x, int z) {

    public static RegionPos of(int x, int z) {
        return new RegionPos(x, z);
    }

    public static RegionPos getByString(String coordinate) {
        String[] split = coordinate.split(",", 2);
        try {
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[1]);
            return new RegionPos(x, z);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
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
        final RegionPos other = (RegionPos) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    @NotNull
    public static RegionPos getByBukkitChunk(@NotNull Chunk chunk) {
        int regionX = (int) Math.floor((double) chunk.getX() / 32.0);
        int regionZ = (int) Math.floor((double) chunk.getZ() / 32.0);
        return new RegionPos(regionX, regionZ);
    }

    @NotNull
    public static RegionPos getByChunkPos(@NotNull ChunkPos chunk) {
        int regionX = (int) Math.floor((double) chunk.x() / 32.0);
        int regionZ = (int) Math.floor((double) chunk.z() / 32.0);
        return new RegionPos(regionX, regionZ);
    }

    @Override
    public String toString() {
        return "RegionPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
