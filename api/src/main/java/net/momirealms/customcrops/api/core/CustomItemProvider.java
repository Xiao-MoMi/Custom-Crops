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

package net.momirealms.customcrops.api.core;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Interface defining methods for managing custom items, blocks, and furniture in the CustomCrops plugin.
 * This interface provides methods for placing and removing custom blocks and furniture, as well as
 * retrieving IDs and ItemStacks associated with custom items.
 */
public interface CustomItemProvider {

    /**
     * Removes a custom block at the specified location.
     *
     * @param location The location from which to remove the custom block.
     * @return true if the block was successfully removed, false otherwise.
     */
    boolean removeCustomBlock(Location location);

    /**
     * Places a custom block at the specified location.
     *
     * @param location The location where the custom block should be placed.
     * @param id       The ID of the custom block to place.
     * @return true if the block was successfully placed, false otherwise.
     */
    boolean placeCustomBlock(Location location, String id);

    /**
     * Places a piece of custom furniture at the specified location.
     *
     * @param location The location where the furniture should be placed.
     * @param id       The ID of the furniture to place.
     * @return The entity representing the placed furniture, or null if placement failed.
     */
    @Nullable
    Entity placeFurniture(Location location, String id);

    /**
     * Removes a piece of custom furniture represented by the specified entity.
     *
     * @param entity The entity representing the furniture to remove.
     * @return true if the furniture was successfully removed, false otherwise.
     */
    boolean removeFurniture(Entity entity);

    /**
     * Retrieves the ID of a custom block at the specified block.
     *
     * @param block The block to check.
     * @return The ID of the custom block, or null if no custom block is found.
     */
    @Nullable
    String blockID(Block block);

    /**
     * Retrieves the ID of a custom item represented by the provided ItemStack.
     *
     * @param itemStack The ItemStack to check.
     * @return The ID of the custom item, or null if no custom item is found.
     */
    @Nullable
    String itemID(ItemStack itemStack);

    /**
     * Creates an ItemStack for a custom item based on the given item ID and player.
     *
     * @param player The player for whom the item is being created.
     * @param id     The ID of the custom item.
     * @return The constructed ItemStack, or null if creation fails.
     */
    @Nullable
    ItemStack itemStack(Player player, String id);

    /**
     * Retrieves the ID of a custom furniture item associated with the specified entity.
     *
     * @param entity The entity to check.
     * @return The ID of the furniture, or null if no furniture is found.
     */
    @Nullable
    String furnitureID(Entity entity);

    /**
     * Determines if the specified entity is a piece of custom furniture.
     *
     * @param entity The entity to check.
     * @return true if the entity is a piece of custom furniture, false otherwise.
     */
    boolean isFurniture(Entity entity);
}
