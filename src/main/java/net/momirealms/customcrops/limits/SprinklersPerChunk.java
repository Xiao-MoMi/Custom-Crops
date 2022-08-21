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

package net.momirealms.customcrops.limits;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.FurnitureUtil;
import org.bukkit.Location;

public class SprinklersPerChunk {

    public static boolean isLimited(Location location){
        if (!ConfigReader.Config.enableLimit) return false;
        int n = 1;
        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16, ConfigReader.Config.yMin,location.getChunk().getZ()*16);
        Label_out:
        for (int i = 0; i < 16; ++i)
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i + 0.5, 0.5, j + 0.5);
                for (int k = ConfigReader.Config.yMin; k <= ConfigReader.Config.yMax; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    if(FurnitureUtil.isSprinkler(square))
                        if (n++ > ConfigReader.Config.sprinklerLimit)
                            break Label_out;
                }
            }
        return n > ConfigReader.Config.sprinklerLimit;
    }
}
