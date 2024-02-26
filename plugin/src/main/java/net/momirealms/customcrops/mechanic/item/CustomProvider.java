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

package net.momirealms.customcrops.mechanic.item;

import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface CustomProvider {

    void removeBlock(Location location);

    void placeCustomBlock(Location location, String id);

    default void placeBlock(Location location, String id) {
        if (ConfigUtils.isVanillaItem(id)) {
            location.getBlock().setType(Material.valueOf(id));
        } else {
            placeCustomBlock(location, id);
        }
    }

    void placeFurniture(Location location, String id);

    String getBlockID(Block block);

    String getItemID(ItemStack itemInHand);

    ItemStack getItemStack(String id);

    String getEntityID(Entity entity);

    default boolean isAir(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR)
            return false;
        Location center = location.toCenterLocation();
        Collection<Entity> entities = center.getWorld().getNearbyEntities(center, 0.5,0.51,0.5);
        entities.removeIf(entity -> entity instanceof Player);
        return entities.size() == 0;
    }
}
