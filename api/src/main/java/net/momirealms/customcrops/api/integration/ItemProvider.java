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

package net.momirealms.customcrops.api.integration;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface representing a provider for custom items.
 * This interface allows for building items for players and retrieving item IDs from item stacks.
 */
public interface ItemProvider extends ExternalProvider {

    /**
     * Builds an ItemStack for a player based on a specified item ID.
     *
     * @param player the player for whom the item is being built.
     * @param id     the ID of the item to build.
     * @return the built ItemStack.
     */
    @NotNull
    ItemStack buildItem(@NotNull Player player, @NotNull String id);

    /**
     * Retrieves the item ID from a given ItemStack.
     *
     * @param itemStack the ItemStack from which to retrieve the item ID.
     * @return the item ID as a string, or null if the item stack does not have an associated ID.
     */
    @Nullable
    String itemID(@NotNull ItemStack itemStack);
}