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

package net.momirealms.customcrops.customplugin.itemsadder;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.customplugin.PlatformInterface;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderPluginImpl implements PlatformInterface {

    @Override
    public boolean removeCustomBlock(Location location) {
        return CustomBlock.remove(location);
    }

    @Nullable
    @Override
    public String getCustomBlockID(Location location) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(location.getBlock());
        return customBlock == null ? null : customBlock.getNamespacedID();
    }

    @Nullable
    @Override
    public ItemStack getItemStack(String id) {
        CustomStack customStack = CustomStack.getInstance(id);
        return customStack == null ? null : customStack.getItemStack();
    }

    @Nullable
    @Override
    public ItemFrame placeItemFrame(Location location, String id) {
        CustomFurniture customFurniture = CustomFurniture.spawn(id, location.getBlock());
        if (customFurniture == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Furniture not exists: " + id);
            return null;
        }
        Entity entity = customFurniture.getArmorstand();
        if (entity instanceof ItemFrame itemFrame)
            return itemFrame;
        else {
            AdventureUtils.consoleMessage("<red>[CustomCrops] ItemFrame not exists: " + id);
            customFurniture.remove(false);
        }
        return null;
    }

    @Nullable
    @Override
    public ItemDisplay placeItemDisplay(Location location, String id) {
        CustomFurniture customFurniture = CustomFurniture.spawn(id, location.getBlock());
        if (customFurniture == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Furniture not exists: " + id);
            return null;
        }
        Entity entity = customFurniture.getArmorstand();
        if (entity instanceof ItemDisplay itemDisplay)
            return itemDisplay;
        else {
            AdventureUtils.consoleMessage("<red>[CustomCrops] ItemDisplay not exists: " + id);
            customFurniture.remove(false);
        }
        return null;
    }

    @Override
    public void placeNoteBlock(Location location, String id) {
        CustomBlock customBlock = CustomBlock.place(id, location);
        if (customBlock == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] NoteBlock not exists: " + id);
        }
    }

    @Override
    public void placeTripWire(Location location, String id) {
        CustomBlock customBlock = CustomBlock.place(id, location);
        if (customBlock == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Tripwire not exists: " + id);
        }
    }

    @NotNull
    @Override
    public String getBlockID(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        return customBlock == null ? block.getType().name() : customBlock.getNamespacedID();
    }

    @Override
    public boolean doesItemExist(String id) {
        return CustomStack.getInstance(id) != null;
    }

    @Override
    public void dropBlockLoot(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) return;
        Location block_loc = block.getLocation();
        for (ItemStack itemStack : customBlock.getLoot()) {
            block_loc.getWorld().dropItemNaturally(block_loc, itemStack);
        }
    }

    @Override
    public void placeChorus(Location location, String id) {
        CustomBlock customBlock = CustomBlock.place(id, location);
        if (customBlock == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Chorus not exists: " + id);
        }
    }

    @NotNull
    @Override
    public String getItemStackID(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
            if (nbtCompound != null) return nbtCompound.getString("namespace") + ":" + nbtCompound.getString("id");
        }
        return itemStack.getType().name();
    }

    @Nullable
    @Override
    public String getItemDisplayID(ItemDisplay itemDisplay) {
        CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(itemDisplay);
        if (customFurniture == null) return null;
        return customFurniture.getNamespacedID();
    }

    @Nullable
    @Override
    public String getItemFrameID(ItemFrame itemFrame) {
        CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(itemFrame);
        if (customFurniture == null) return null;
        return customFurniture.getNamespacedID();
    }
}
