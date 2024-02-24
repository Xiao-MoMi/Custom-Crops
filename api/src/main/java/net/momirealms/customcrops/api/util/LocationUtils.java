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

package net.momirealms.customcrops.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    private LocationUtils() {}

    /**
     * Calculates the Euclidean distance between two locations in 3D space.
     *
     * @param location1 The first location
     * @param location2 The second location
     * @return The Euclidean distance between the two locations
     */
    public static double getDistance(Location location1, Location location2) {
        return Math.sqrt(Math.pow(location2.getX() - location1.getX(), 2) +
                Math.pow(location2.getY() - location1.getY(), 2) +
                Math.pow(location2.getZ() - location1.getZ(), 2)
        );
    }

    public static Location getAnyLocationInstance() {
        return new Location(Bukkit.getWorlds().get(0), 0, 64, 0);
    }
}
