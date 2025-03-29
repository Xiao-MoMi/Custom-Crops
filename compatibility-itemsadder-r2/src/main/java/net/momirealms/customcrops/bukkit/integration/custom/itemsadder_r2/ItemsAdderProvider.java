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

package net.momirealms.customcrops.bukkit.integration.custom.itemsadder_r1;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.CustomItemProvider;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderProvider implements CustomItemProvider {

    @Override
    public boolean removeCustomBlock(Location location) {
        return CustomBlock.remove(location);
    }

    @Override
    public boolean placeCustomBlock(Location location, String id) {
        CustomBlock block = CustomBlock.place(id, location);
        if (block == null) {
            CustomStack furniture = CustomFurniture.getInstance(id);
            if (furniture == null) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Detected that custom block[" + id + "] doesn't exist in ItemsAdder configs. Please double check if that block exists.");
            } else {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Detected that you mistakenly configured the custom block[" + id + "] as furniture.");
            }
            return false;
        }
        return true;
    }

    @Override
    public Entity placeFurniture(Location location, String id) {
        try {
            CustomFurniture furniture = CustomFurniture.spawnPreciseNonSolid(id, LocationUtils.toSurfaceCenterLocation(location));
            if (furniture == null) return null;
            return furniture.getEntity();
        } catch (RuntimeException e) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Failed to place furniture[" + id + "]. If this is not a problem caused by furniture not existing, consider increasing max-furniture-vehicles-per-chunk in ItemsAdder config.yml.", e);
            return null;
        }
    }

    @Override
    public boolean removeFurniture(Entity entity) {
        if (isFurniture(entity)) {
            CustomFurniture.remove(entity, false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String blockID(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) {
            return null;
        }
        return customBlock.getNamespacedID();
    }

    @Override
    public String itemID(ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null) {
            return null;
        }
        return customStack.getNamespacedID();
    }

    @Override
    public ItemStack itemStack(Player player, String id) {
        if (id == null) return new ItemStack(Material.AIR);
        CustomStack customStack = CustomStack.getInstance(id);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack().clone();
    }

    @Override
    public String furnitureID(Entity entity) {
        CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(entity);
        if (customFurniture == null) {
            return null;
        }
        return customFurniture.getNamespacedID();
    }

    @Override
    public boolean isFurniture(Entity entity) {
        try {
            return CustomFurniture.byAlreadySpawned(entity) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
