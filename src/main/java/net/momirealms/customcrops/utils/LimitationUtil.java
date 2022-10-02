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

package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;

public class LimitationUtil {

    public static boolean reachWireLimit(Location location) {
        int minHeight = location.getWorld().getMinHeight();
        int maxHeight = location.getWorld().getMaxHeight();
        Location chunkLocation = new Location(location.getWorld(), location.getChunk().getX() * 16, minHeight, location.getChunk().getZ() * 16);
        int n = 0;
        for (int i = 0; i < 16; ++i){
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i, 0, j);
                for (int k = minHeight; k <= maxHeight; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    if (square.getBlock().getType() == Material.TRIPWIRE && ++n > MainConfig.wireAmount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean reachFrameLimit(Location location) {
        int minHeight = location.getWorld().getMinHeight();
        int maxHeight = location.getWorld().getMaxHeight();
        Location chunkLocation = new Location(location.getWorld(), location.getChunk().getX() * 16, minHeight, location.getChunk().getZ() * 16);
        int n = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i + 0.5, 0.5, j + 0.5);
                for (int k = minHeight; k <= maxHeight; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    if (FurnitureUtil.hasFurniture(square) && ++n > MainConfig.frameAmount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}