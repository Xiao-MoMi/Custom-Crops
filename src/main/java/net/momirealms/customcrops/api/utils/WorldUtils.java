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


package net.momirealms.customcrops.api.utils;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.World;

public class WorldUtils {

    /**
     * load a world's crop data
     * @param world world
     */
    public static void loadCropWorld(World world) {
        CustomCrops.plugin.getCropManager().onWorldLoad(world);
    }

    /**
     * unload a world's crop data
     * @param world world
     * @param sync whether unload is sync or async
     */
    public static void unloadCropWorld(World world, boolean sync) {
        CustomCrops.plugin.getCropManager().onWorldUnload(world, sync);
    }
}
