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

import net.momirealms.customcrops.common.item.Item;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface defining methods for managing custom items, blocks, and furniture in the CustomCrops plugin.
 * It includes methods for placing and removing items and blocks, interacting with custom events, and managing item data.
 */
public interface ItemManager extends Reloadable {

    /**
     * Places an item at the specified location.
     *
     * @param location The location where the item should be placed.
     * @param form     The existence form of the item (e.g., block or furniture).
     * @param id       The ID of the item to place.
     * @param rotation The rotation of the furniture, if applicable.
     */
    void place(@NotNull Location location, @NotNull ExistenceForm form, @NotNull String id, FurnitureRotation rotation);

    /**
     * Removes an item from the specified location.
     *
     * @param location The location from which the item should be removed.
     * @param form     The existence form of the item (e.g., block or furniture).
     * @return The rotation of the removed furniture, or NONE if no furniture was found.
     */
    @NotNull
    FurnitureRotation remove(@NotNull Location location, @NotNull ExistenceForm form);

    /**
     * Places a custom block at the specified location.
     *
     * @param location The location where the block should be placed.
     * @param id       The ID of the block to place.
     */
    void placeBlock(@NotNull Location location, @NotNull String id);

    /**
     * Places a piece of furniture at the specified location.
     *
     * @param location The location where the furniture should be placed.
     * @param id       The ID of the furniture to place.
     * @param rotation The rotation of the furniture.
     */
    void placeFurniture(@NotNull Location location, @NotNull String id, FurnitureRotation rotation);

    /**
     * Removes a custom block from the specified location.
     *
     * @param location The location from which the block should be removed.
     */
    void removeBlock(@NotNull Location location);

    /**
     * Removes furniture from the specified location.
     *
     * @param location The location from which the furniture should be removed.
     * @return The rotation of the removed furniture, or NONE if no furniture was found.
     */
    @NotNull
    FurnitureRotation removeFurniture(@NotNull Location location);

    /**
     * Retrieves the ID of the custom block at the specified location.
     *
     * @param location The location of the block.
     * @return The ID of the block.
     */
    @NotNull
    default String blockID(@NotNull Location location) {
        return blockID(location.getBlock());
    }

    /**
     * Retrieves the ID of the custom block at the specified block.
     *
     * @param block The block to check.
     * @return The ID of the block.
     */
    @NotNull
    String blockID(@NotNull Block block);

    /**
     * Retrieves the ID of the furniture attached to the specified entity.
     *
     * @param entity The entity to check.
     * @return The ID of the furniture, or null if not applicable.
     */
    @Nullable
    String furnitureID(@NotNull Entity entity);

    /**
     * Retrieves the ID of the custom entity.
     *
     * @param entity The entity to check.
     * @return The ID of the entity.
     */
    @NotNull String entityID(@NotNull Entity entity);

    /**
     * Retrieves the ID of the furniture at the specified location.
     *
     * @param location The location to check.
     * @return The ID of the furniture, or null if no furniture is found.
     */
    @Nullable
    String furnitureID(Location location);

    /**
     * Retrieves the ID of any custom item at the specified location.
     *
     * @param location The location to check.
     * @return The ID of any custom item.
     */
    @NotNull
    String anyID(Location location);

    /**
     * Retrieves the ID of the item at the specified location based on the existence form.
     *
     * @param location The location to check.
     * @param form     The existence form of the item.
     * @return The ID of the item, or null if not found.
     */
    @Nullable
    String id(Location location, ExistenceForm form);

    /**
     * Sets a custom event listener for handling custom item events.
     *
     * @param listener The custom event listener to set.
     */
    void setCustomEventListener(@NotNull AbstractCustomEventListener listener);

    /**
     * Sets a custom item provider for managing custom items.
     *
     * @param provider The custom item provider to set.
     */
    void setCustomItemProvider(@NotNull CustomItemProvider provider);

    /**
     * Retrieves the ID of the custom item represented by the provided ItemStack.
     *
     * @param itemStack The ItemStack to check.
     * @return The ID of the custom item.
     */
    @NotNull
    String id(@Nullable ItemStack itemStack);

    /**
     * Builds an ItemStack for the player based on the given item ID.
     *
     * @param player The player for whom the item is being built.
     * @param id     The ID of the item to build.
     * @return The constructed ItemStack, or null if construction fails.
     */
    @Nullable
    ItemStack build(@Nullable Player player, @NotNull String id);

    /**
     * Wraps an ItemStack into a custom item wrapper.
     *
     * @param itemStack The ItemStack to wrap.
     * @return The wrapped custom item.
     */
    Item<ItemStack> wrap(@NotNull ItemStack itemStack);

    /**
     * Decreases the damage on the provided item.
     *
     * @param player    The item.
     * @param itemStack The item to modify.
     * @param amount    The amount of damage to decrease.
     */
    void decreaseDamage(Player player, ItemStack itemStack, int amount);

    /**
     * Increases the damage on the provided item.
     *
     * @param holder    The item.
     * @param itemStack The item to modify.
     * @param amount    The amount of damage to increase.
     */
    void increaseDamage(Player holder, ItemStack itemStack, int amount);
}
