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

package net.momirealms.customcrops.bukkit.integration.custom.oraxen_r1;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.CustomItemProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public class OraxenProvider implements CustomItemProvider {

    @Override
    public boolean removeCustomBlock(Location location) {
        Block block = location.getBlock();
        if (OraxenBlocks.isOraxenBlock(block)) {
            block.setType(Material.AIR, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean placeCustomBlock(Location location, String id) {
        if (OraxenItems.exists(id)) {
            removeCustomBlock(location);
            OraxenBlocks.place(id, location);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable Entity placeFurniture(Location location, String id) {
        Entity entity = OraxenFurniture.place(id, location, Rotation.NONE, BlockFace.UP);
        if (entity == null) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Furniture[" + id +"] doesn't exist. Please double check if that furniture exists.");
        }
        return entity;
    }

    @Override
    public boolean removeFurniture(Entity entity) {
        if (isFurniture(entity)) {
            OraxenFurniture.remove(entity, null, null);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable String blockID(Block block) {
        Mechanic mechanic = OraxenBlocks.getOraxenBlock(block.getBlockData());
        if (mechanic == null) {
            return null;
        }
        return mechanic.getItemID();
    }

    @Override
    public @Nullable String itemID(ItemStack itemStack) {
        return OraxenItems.getIdByItem(itemStack);
    }

    @Override
    public @Nullable ItemStack itemStack(Player player, String id) {
        ItemBuilder builder = OraxenItems.getItemById(id);
        if (builder == null) {
            return null;
        }
        return builder.build();
    }

    @Override
    public @Nullable String furnitureID(Entity entity) {
        FurnitureMechanic mechanic = OraxenFurniture.getFurnitureMechanic(entity);
        if (mechanic == null) {
            return null;
        }
        return mechanic.getItemID();
    }

    @Override
    public boolean isFurniture(Entity entity) {
        return OraxenFurniture.isFurniture(entity);
    }
}
