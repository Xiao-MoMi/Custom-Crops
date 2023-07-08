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

package net.momirealms.customcrops.customplugin;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.ItemMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PlatformInterface {

    /**
     * This method is used for removing custom blocks
     * @param location location
     * @return false if it is not a custom one
     */
    boolean removeCustomBlock(Location location);

    /**
     * This method is used for removing any block
     * @param location location
     * @return false if there's no block
     */
    default boolean removeAnyBlock(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR) {
            return false;
        }
        if (!removeCustomBlock(location)) {
            block.setType(Material.AIR);
        }
        return true;
    }

    default boolean removeAnyBlock(Block block) {
        if (block.getType() == Material.AIR) {
            return false;
        }
        if (!removeCustomBlock(block.getLocation())) {
            block.setType(Material.AIR);
        }
        return true;
    }

    /**
     * Get custom block id at a certain location
     * @param location location
     * @return block id
     */
    @Nullable
    String getCustomBlockID(Location location);

    /**
     * Get item by id
     * @param id id
     * @return itemStack
     */
    @Nullable
    ItemStack getItemStack(String id);

    /**
     * Place an item frame at the specified location
     * Would remove the entity if it is not item frame
     * @param location location
     * @param id id
     * @return item frame
     */
    @Nullable
    ItemFrame placeItemFrame(Location location, String id);

    /**
     * Place an item display at the specified location
     * Would remove the entity if it is not item display
     * @param location location
     * @param id id
     * @return item display
     */
    @Nullable
    ItemDisplay placeItemDisplay(Location location, String id);

    /**
     * Place custom note block at a specified location
     * @param location location
     * @param id id
     */
    void placeNoteBlock(Location location, String id);

    /**
     * Place custom string block at a specified location
     * @param location location
     * @param id id
     */
    void placeTripWire(Location location, String id);

    /**
     * Place custom chorus plant at a specified location
     * @param location location
     * @param id id
     */
    void placeChorus(Location location, String id);

    /**
     * Get the block id
     * (Examples)
     *   Vanilla stone -> STONE
     *   ItemsAdder pot -> customcrops:pot
     *   Oraxen pot -> pot
     * @param block block
     * @return id
     */
    @NotNull
    String getBlockID(Block block);

    /**
     * If an item exists in item library
     * @param id id
     * @return exists or not
     */
    boolean doesItemExist(String id);

    /**
     * Drop the block's loot
     * @param block block
     */
    void dropBlockLoot(Block block);

    /**
     * Get the custom stuff at a specified location
     * It might be a block or an entity
     * @param location location
     * @return id
     */
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

    /**
     * Remove all the custom stuff at a specified location
     * @param location location
     */
    default void removeCustomItemAt(Location location) {
        removeAnyBlock(location);
        removeItemFrame(location);
        if (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3()) {
            removeItemDisplay(location);
        }
    }

    /**
     * Get itemStack's internal id
     * @param itemStack itemStack
     * @return id
     */
    @NotNull
    String getItemStackID(@NotNull ItemStack itemStack);

    /**
     * Get item display at a specified location
     * This method would also remove overlapped entities
     * @param location location
     * @return id
     */
    @Nullable
    default String getItemDisplayIDAt(Location location) {
        ItemDisplay itemDisplay = getItemDisplayAt(location);
        if (itemDisplay == null) return null;
        return getItemDisplayID(itemDisplay);
    }

    /**
     * Get item frame at a specified location
     * This method would also remove overlapped entities
     * @param location location
     * @return id
     */
    @Nullable
    default String getItemFrameIDAt(Location location) {
        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame == null) return null;
        return getItemFrameID(itemFrame);
    }

    /**
     * Get custom furniture's id
     * @param itemDisplay itemDisplay
     * @return id
     */
    @Nullable
    String getItemDisplayID(ItemDisplay itemDisplay);

    /**
     * Get custom furniture's id
     * @param itemFrame itemFrame
     * @return id
     */
    @Nullable
    String getItemFrameID(ItemFrame itemFrame);

    /**
     * Get item frame at a specified location
     * This method would also remove overlapped entities
     * @param location location
     * @return id
     */
    @Nullable
    default ItemFrame getItemFrameAt(Location location) {
        Collection<ItemFrame> itemFrames = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(ItemFrame.class, 0.4, 0.5, 0.4);
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

    /**
     * Get item display at a specified location
     * This method would also remove overlapped entities
     * @param location location
     * @return id
     */
    @Nullable
    default ItemDisplay getItemDisplayAt(Location location) {
        Collection<ItemDisplay> itemDisplays = location.clone().add(0.5,0,0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.4, 0.5, 0.4);
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

    /**
     * Remove item frames at a specified location
     * @param location location
     * @return success or not
     */
    default boolean removeItemFrame(Location location) {
        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame != null) {
            itemFrame.remove();
            if (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3()) removeInteractions(location);
            return true;
        }
        return false;
    }

    /**
     * Remove item display entities at a specified location
     * @param location location
     * @return success or not
     */
    default boolean removeItemDisplay(Location location) {
        ItemDisplay itemDisplay = getItemDisplayAt(location);
        if (itemDisplay != null) {
            itemDisplay.remove();
            removeInteractions(location);
            return true;
        }
        return false;
    }

    /**
     * Remove interaction entities at a specified location
     * @param location location
     * @return success or not
     */
    default boolean removeInteractions(Location location) {
        Collection<Interaction> interactions = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(Interaction.class, 0.4, 0.5, 0.4);
        for (Interaction interaction : interactions) {
            interaction.remove();
        }
        return interactions.size() != 0;
    }

    /**
     * If there's any custom stuff at a specified location
     * @param location location
     * @return has custom stuff or not
     */
    default boolean detectAnyThing(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) return true;
        Collection<Entity> entities = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(ItemFrame.class, 0.4, 0.5, 0.4);
        return entities.size() != 0 || (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3() && detectItemDisplay(location));
    }

    /**
     * If there's any item display entity at a specified location
     * @param location location
     * @return has item display or not
     */
    default boolean detectItemDisplay(Location location) {
        Collection<Entity> entities = location.clone().add(0.5,0,0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.4, 0.5, 0.4);
        return entities.size() != 0;
    }

    /**
     * Place custom stuff according to its mode
     * @param location location
     * @param itemMode itemMode
     */
    default void placeCustomItem(Location location, String id, ItemMode itemMode) {
        if (itemMode == ItemMode.TRIPWIRE || itemMode == ItemMode.NOTE_BLOCK)
            placeTripWire(location, id);
        else if (itemMode == ItemMode.ITEM_FRAME)
            placeItemFrame(location, id);
        else if (itemMode == ItemMode.ITEM_DISPLAY)
            placeItemDisplay(location, id);
        else if (itemMode == ItemMode.CHORUS)
            placeChorus(location, id);
    }

    /**
     * Remove custom stuff according to its mode
     * @param location location
     * @param itemMode itemMode
     * @return success or not
     */
    default boolean removeCustomItem(Location location, ItemMode itemMode) {
        if (itemMode == ItemMode.TRIPWIRE || itemMode == ItemMode.CHORUS || itemMode == ItemMode.NOTE_BLOCK)
            return removeCustomBlock(location);
        else if (itemMode == ItemMode.ITEM_FRAME)
            return removeItemFrame(location);
        else if (itemMode == ItemMode.ITEM_DISPLAY)
            return removeItemDisplay(location);
        return false;
    }

    /**
     * Remove anything
     * @param location location
     */
    default void removeAnyThingAt(Location location) {
        removeAnyBlock(location);
        removeItemFrame(location);
        if (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3()) removeItemDisplay(location);
    }
}
