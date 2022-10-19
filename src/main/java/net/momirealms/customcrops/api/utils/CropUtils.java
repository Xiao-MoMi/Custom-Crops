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
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.CropConfig;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class CropUtils {

    /**
     * get a crop config
     * @param crop crop
     * @return crop config
     */
    @Nullable
    public static Crop getCrop(String crop) {
        return CropConfig.CROPS.get(crop);
    }

    /**
     * whether planting succeeds
     * @param location location
     * @param crop crop
     * @return
     */
    public static boolean plantCrop(Location location, String crop) {
        return CustomCrops.plugin.getCropManager().getHandler().plantSeed(location, crop, null, null);
    }
}
