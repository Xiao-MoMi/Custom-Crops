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

package net.momirealms.customcrops.bukkit.integration.custom.crucible_r1;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythiccrucible.MythicCrucible;
import io.lumine.mythiccrucible.items.CrucibleItem;
import io.lumine.mythiccrucible.items.ItemManager;
import io.lumine.mythiccrucible.items.blocks.CustomBlockItemContext;
import io.lumine.mythiccrucible.items.blocks.CustomBlockManager;
import io.lumine.mythiccrucible.items.furniture.Furniture;
import io.lumine.mythiccrucible.items.furniture.FurnitureManager;
import net.momirealms.customcrops.api.core.CustomItemProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class CrucibleProvider implements CustomItemProvider {

    private final ItemManager itemManager;
    private final CustomBlockManager blockManager;
    private final FurnitureManager furnitureManager;

    public CrucibleProvider() {
        this.itemManager = MythicCrucible.inst().getItemManager();
        this.blockManager = itemManager.getCustomBlockManager();
        this.furnitureManager = itemManager.getFurnitureManager();
    }

    @Override
    public boolean removeCustomBlock(Location location) {
        Block block = location.getBlock();
        Optional<CustomBlockItemContext> optionalContext = blockManager.getBlockFromBlock(block);
        if (optionalContext.isPresent()) {
            block.setType(Material.AIR, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean placeCustomBlock(Location location, String id) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(id);
        if (optionalCI.isPresent()) {
            location.getBlock().setBlockData(optionalCI.get().getBlockData().getBlockData(), false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable Entity placeFurniture(Location location, String id) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(id);
        return optionalCI.map(crucibleItem -> crucibleItem.getFurnitureData().placeFrame(location.getBlock(), BlockFace.UP, 0f, null)).orElse(null);
    }

    @Override
    public boolean removeFurniture(Entity entity) {
        if (isFurniture(entity)) {
            Optional<Furniture> optionalFurniture = furnitureManager.getFurniture(new BukkitEntity(entity));
            if (optionalFurniture.isPresent()) {
                Furniture furniture = optionalFurniture.get();
                furniture.getFurnitureData().remove(furniture, null, false, false);
            }
            return true;
        } else if (entity instanceof Interaction interaction) {
            Optional<Furniture> optionalFurniture = furnitureManager.getFurniture(interaction);
            if (optionalFurniture.isPresent()) {
                Furniture furniture = optionalFurniture.get();
                furniture.getFurnitureData().remove(furniture, null, false, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable String blockID(Block block) {
        Optional<CustomBlockItemContext> optionalContext = blockManager.getBlockFromBlock(block);
        return optionalContext.map(customBlockItemContext -> customBlockItemContext.getCrucibleItem().getInternalName()).orElse(null);
    }

    @Override
    public @Nullable String itemID(ItemStack itemStack) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(itemStack);
        return optionalCI.map(CrucibleItem::getInternalName).orElse(null);
    }

    @Override
    public @Nullable ItemStack itemStack(Player player, String id) {
        Optional<CrucibleItem> optionalCI = itemManager.getItem(id);
        return optionalCI.map(crucibleItem -> BukkitAdapter.adapt(crucibleItem.getMythicItem().generateItemStack(1))).orElse(null);
    }

    @Override
    public @Nullable String furnitureID(Entity entity) {
        if (entity instanceof Interaction interaction) {
            Optional<Furniture> optionalFurniture = furnitureManager.getFurniture(interaction);
            return optionalFurniture.map(furniture -> furniture.getFurnitureData().getItem().getInternalName()).orElse(null);
        } else {
            Optional<Furniture> optionalFurniture = furnitureManager.getFurniture(new BukkitEntity(entity));
            return optionalFurniture.map(furniture -> furniture.getFurnitureData().getItem().getInternalName()).orElse(null);
        }
    }

    @Override
    public boolean isFurniture(Entity entity) {
        return furnitureManager.isFurniture(new BukkitEntity(entity));
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public CustomBlockManager getBlockManager() {
        return blockManager;
    }

    public FurnitureManager getFurnitureManager() {
        return furnitureManager;
    }
}
