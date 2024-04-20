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

package net.momirealms.customcrops.api.integration;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemLibrary {

    /**
     * Get the identification
     * for instance "CustomItems"
     *
     * @return identification
     */
    String identification();

    /**
     * Build an item instance for a player
     *
     * @param player player
     * @param id id
     * @return item
     */
    ItemStack buildItem(Player player, String id);

    /**
     * Get an item's id
     *
     * @param itemStack item
     * @return ID
     */
    String getItemID(ItemStack itemStack);
}
