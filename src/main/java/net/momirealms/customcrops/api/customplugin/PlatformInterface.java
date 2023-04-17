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

package net.momirealms.customcrops.api.customplugin;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PlatformInterface {

    boolean removeCustomBlock(Location location);

    @Nullable
    String getCustomBlockID(Location location);

    @Nullable
    ItemStack getItemStack(String id);

    @Nullable
    ItemFrame placeItemFrame(Location location, String id);

    @Nullable
    ItemDisplay placeItemDisplay(Location location, String id);

    void placeNoteBlock(Location location, String id);

    void placeTripWire(Location location, String id);

    @NotNull
    String getBlockID(Block block);

    boolean doesItemExist(String id);

    void dropBlockLoot(Block block);

    void placeChorus(Location location, String id);

    Location getItemFrameLocation(Location location);

    @NotNull
    default String getAnyItemIDAt(Location location) {
        String block = getBlockID(location.getBlock());
        if (!block.equals("AIR")) return block;

        String item_frame_id = getItemFrameIDAt(location);
        if (item_frame_id != null) {
            return item_frame_id;
        }

        if (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3()) {
            String item_display_id = getItemDisplayIDAt(location);
            if (item_display_id != null) {
                return item_display_id;
            }
        }
        return "AIR";
    }

    default void removeCustomItemAt(Location location) {
        removeCustomBlock(location);
        removeItemFrame(location);
    }

    @NotNull
    String getItemStackID(@NotNull ItemStack itemStack);

    @Nullable
    default String getItemDisplayIDAt(Location location) {
        ItemDisplay itemDisplay = getItemDisplayAt(location);
        if (itemDisplay == null) return null;
        return getItemDisplayID(itemDisplay);
    }

    @Nullable
    default String getItemFrameIDAt(Location location) {
        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame == null) return null;
        return getItemFrameID(itemFrame);
    }

    @Nullable
    String getItemDisplayID(ItemDisplay itemDisplay);

    @Nullable
    String getItemFrameID(ItemFrame itemFrame);

    @Nullable
    default ItemFrame getItemFrameAt(Location location) {
        Collection<ItemFrame> itemFrames = getItemFrameLocation(location).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0.5, 0.5);
        int i = itemFrames.size();
        int j = 1;
        for (ItemFrame itemFrame : itemFrames) {
            if (j != i) {
                itemFrame.remove();
                j++;
            }
            else return itemFrame;
        }
        return null;
    }

    @Nullable
    default ItemDisplay getItemDisplayAt(Location location) {
        Collection<ItemDisplay> itemDisplays = getItemFrameLocation(location).getNearbyEntitiesByType(ItemDisplay.class, 0.5, 0.5, 0.5);
        int i = itemDisplays.size();
        int j = 1;
        for (ItemDisplay itemDisplay : itemDisplays) {
            if (j != i) {
                itemDisplay.remove();
                j++;
            }
            else return itemDisplay;
        }
        return null;
    }

    default boolean removeItemFrame(Location location) {
        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame != null) {
            itemFrame.remove();
            return true;
        }
        return false;
    }

    default boolean removeItemDisplay(Location location) {
        ItemDisplay itemDisplay = getItemDisplayAt(location);
        if (itemDisplay != null) {
            itemDisplay.remove();
            return true;
        }
        return false;
    }

    default boolean detectAnyThing(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) return true;
        Collection<Entity> entities = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0.5, 0.5);
        return entities.size() != 0 || (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3() && detectItemDisplay(location));
    }

    default boolean detectItemDisplay(Location location) {
        Collection<Entity> entities = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.5, 0.5, 0.5);
        return entities.size() != 0;
    }
}
