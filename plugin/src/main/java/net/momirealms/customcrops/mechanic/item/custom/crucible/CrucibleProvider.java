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

package net.momirealms.customcrops.mechanic.item.custom.crucible;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythiccrucible.MythicCrucible;
import io.lumine.mythiccrucible.items.CrucibleItem;
import io.lumine.mythiccrucible.items.ItemManager;
import io.lumine.mythiccrucible.items.blocks.CustomBlockItemContext;
import io.lumine.mythiccrucible.items.blocks.CustomBlockManager;
import io.lumine.mythiccrucible.items.furniture.Furniture;
import io.lumine.mythiccrucible.items.furniture.FurnitureManager;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.item.CustomProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CrucibleProvider implements CustomProvider {

    private final ItemManager itemManager;
    private final CustomBlockManager blockManager;
    private final FurnitureManager furnitureManager;

    public CrucibleProvider() {
        this.itemManager = MythicCrucible.inst().getItemManager();
        this.blockManager = itemManager.getCustomBlockManager();
        this.furnitureManager = itemManager.getFurnitureManager();
    }

    @Override
    public boolean removeBlock(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR) {
            return false;
        }
        Optional<CustomBlockItemContext> optional = blockManager.getBlockFromBlock(block);
        if (optional.isPresent()) {
            optional.get().remove(block, null, false);
        } else {
            block.setType(Material.AIR);
        }
        return true;
    }

    @Override
    public void placeCustomBlock(Location location, String id) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(id);
        if (optionalCI.isPresent()) {
            location.getBlock().setBlockData(optionalCI.get().getBlockData().getBlockData());
        } else {
            LogUtils.warn("Custom block(" + id +") doesn't exist in Crucible configs. Please double check if that block exists.");
        }
    }

    @Override
    public Entity placeFurniture(Location location, String id) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(id);
        if (optionalCI.isPresent()) {
            return optionalCI.get().getFurnitureData().placeFrame(location.getBlock(), BlockFace.UP, 0f, null);
        } else {
            LogUtils.warn("Furniture(" + id +") doesn't exist in Crucible configs. Please double check if that furniture exists.");
            return null;
        }
    }

    @Override
    public void removeFurniture(Entity entity) {
        Optional<Furniture> optional = furnitureManager.getFurniture(entity.getUniqueId());
        optional.ifPresent(furniture -> furniture.getFurnitureData().remove(furniture, null, false, false));
    }

    @Override
    public String getBlockID(Block block) {
        Optional<CustomBlockItemContext> optionalCB = blockManager.getBlockFromBlock(block);
        return optionalCB.map(customBlockItemContext -> customBlockItemContext.getCrucibleItem().getInternalName()).orElse(block.getType().name());
    }

    @Override
    public String getItemID(ItemStack itemStack) {
        return itemManager.getItem(itemStack).map(CrucibleItem::getInternalName).orElse(null);
    }

    @Override
    public ItemStack getItemStack(String id) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(id);
        return optionalCI.map(crucibleItem -> BukkitAdapter.adapt(crucibleItem.getMythicItem().generateItemStack(1))).orElse(null);
    }

    @Override
    public String getEntityID(Entity entity) {
        Optional<CrucibleItem> optionalCI = furnitureManager.getItemFromEntity(entity);
        if (optionalCI.isPresent()) {
            return optionalCI.get().getInternalName();
        }
        return entity.getType().name();
    }

    @Override
    public boolean isFurniture(Entity entity) {
        return furnitureManager.isFurniture(new BukkitEntity(entity));
    }
}
