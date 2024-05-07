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

package net.momirealms.customcrops.mechanic.item.custom.oraxen;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import net.momirealms.customcrops.api.mechanic.item.custom.CustomProvider;
import net.momirealms.customcrops.api.util.LogUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class OraxenProvider implements CustomProvider {

    @Override
    public boolean removeBlock(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR) {
            return false;
        }
        block.setType(Material.AIR);
        return true;
    }

    @Override
    public void placeCustomBlock(Location location, String id) {
        OraxenBlocks.place(id, location);
    }

    @Override
    public Entity placeFurniture(Location location, String id) {
        Entity entity = OraxenFurniture.place(id, location, Rotation.NONE, BlockFace.UP);
        if (entity == null) {
            LogUtils.warn("Furniture(" + id +") doesn't exist in Oraxen configs. Please double check if that furniture exists.");
        }
        return entity;
    }

    @Override
    public void removeFurniture(Entity entity) {
        OraxenFurniture.remove(entity, null);
    }

    @Override
    public String getBlockID(Block block) {
        Mechanic mechanic = OraxenBlocks.getCustomBlockMechanic(block.getLocation());
        if (mechanic == null) {
            return block.getType().name();
        }
        return mechanic.getItemID();
    }

    @Override
    public String getItemID(ItemStack itemStack) {
        return OraxenItems.getIdByItem(itemStack);
    }

    @Override
    public ItemStack getItemStack(String id) {
        if (id == null) return new ItemStack(Material.AIR);
        ItemBuilder builder = OraxenItems.getItemById(id);
        if (builder == null) {
            return null;
        }
        return builder.build();
    }

    @Override
    public String getEntityID(Entity entity) {
        FurnitureMechanic mechanic = OraxenFurniture.getFurnitureMechanic(entity);
        if (mechanic == null) {
            return entity.getType().name();
        }
        return mechanic.getItemID();
    }

    @Override
    public boolean isFurniture(Entity entity) {
        return OraxenFurniture.isFurniture(entity);
    }
}
