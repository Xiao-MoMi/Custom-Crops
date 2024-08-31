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

public record Pos3(int x, int y, int z) {

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }

    public static Pos3 from(Location location) {
        return new Pos3(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public Pos3 add(int x, int y, int z) {
        return new Pos3(this.x + x, this.y + y, this.z + z);
    }

    public ChunkPos toChunkPos() {
        return ChunkPos.fromPos3(this);
    }

    public int chunkX() {
        return (int) Math.floor((double) this.x() / 16.0);
    }

    public int chunkZ() {
        return (int) Math.floor((double) this.z() / 16.0);
    }

    @Override
    public String toString() {
        return "Pos3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}