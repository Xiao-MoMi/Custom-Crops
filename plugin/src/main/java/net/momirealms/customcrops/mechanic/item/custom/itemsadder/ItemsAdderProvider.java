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

package net.momirealms.customcrops.mechanic.item.custom.itemsadder;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.mechanic.item.CustomProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderProvider implements CustomProvider {

    @Override
    public void removeBlock(Location location) {
        if (!CustomBlock.remove(location)) {
            location.getBlock().setType(Material.AIR);
        }
    }

    @Override
    public void placeCustomBlock(Location location, String id) {
        CustomBlock.place(id, location);
    }

    @Override
    public void placeFurniture(Location location, String id) {
        CustomFurniture.spawnPreciseNonSolid(id, location);
    }

    @Override
    public String getBlockID(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) {
            return block.getType().name();
        }
        return customBlock.getNamespacedID();
    }

    @Override
    public String getItemID(ItemStack itemInHand) {
        CustomStack customStack = CustomStack.byItemStack(itemInHand);
        if (customStack == null) {
            return itemInHand.getType().name();
        }
        return customStack.getNamespacedID();
    }

    @Override
    public ItemStack getItemStack(String id) {
        CustomStack customStack = CustomStack.getInstance(id);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }

    @Override
    public String getEntityID(Entity entity) {
        CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(entity);
        if (customFurniture == null) {
            return entity.getType().name();
        }
        return customFurniture.getNamespacedID();
    }
}
