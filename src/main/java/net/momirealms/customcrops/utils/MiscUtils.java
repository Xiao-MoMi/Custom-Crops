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

import net.momirealms.customcrops.objects.SimpleLocation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class MiscUtils {

    public static SimpleLocation getSimpleLocation(Location location) {
        return new SimpleLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Nullable
    public static Location getLocation(SimpleLocation location) {
        World world = Bukkit.getWorld(location.getWorldName());
        if (world == null) return null;
        return new Location(world, location.getX(), location.getY(), location.getZ());
    }

    public static SimpleLocation getSimpleLocation(String location, String world) {
        String[] loc = StringUtils.split(location, ",");
        return new SimpleLocation(world, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
    }

    public static Location getLocation(String location, World world) {
        String[] loc = StringUtils.split(location, ",");
        return new Location(world, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
    }

    public static String getStringLocation(Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}
