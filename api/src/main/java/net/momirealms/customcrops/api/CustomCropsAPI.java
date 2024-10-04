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

package net.momirealms.customcrops.api;

import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public interface CustomCropsAPI {

    /**
     * Adapts a Bukkit Location to a Pos3
     *
     * @param location location
     * @return pos3
     */
    Pos3 adapt(Location location);

    /**
     * Gets the CustomCrops world
     *
     * @param name world name
     * @return the world
     */
    @Nullable CustomCropsWorld<?> getCustomCropsWorld(String name);

    /**
     * Gets the CustomCrops world
     *
     * @param world Bukkit world
     * @return the world
     */
    @Nullable CustomCropsWorld<?> getCustomCropsWorld(World world);

    /**
     * Adds point to a crop at certain location
     *
     * @param location location
     * @param point point to add
     */
    void addPointToCrop(Location location, int point);

    /**
     * Places a crop regardless of planting conditions such as pots.
     *
     * @param location location
     * @param id crop id
     * @param point point
     * @return success or not
     */
    boolean placeCrop(Location location, String id, int point);

    /**
     * Performs actions to destroy crops on behalf of the player.
     *
     * @param player player
     * @param hand hand
     * @param location location
     * @param reason reason
     */
    void simulatePlayerBreakCrop(Player player, EquipmentSlot hand, Location location, BreakReason reason);
}
