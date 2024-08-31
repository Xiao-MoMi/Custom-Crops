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

package net.momirealms.customcrops.api.core.wrapper;

import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class WrappedInteractAirEvent {

    private final CustomCropsWorld<?> world;
    private final ItemStack itemInHand;
    private final String itemID;
    private final EquipmentSlot hand;
    private final Player player;

    public WrappedInteractAirEvent(CustomCropsWorld<?> world, Player player, EquipmentSlot hand, ItemStack itemInHand, String itemID) {
        this.world = world;
        this.itemInHand = itemInHand;
        this.itemID = itemID;
        this.hand = hand;
        this.player = player;
    }

    public CustomCropsWorld<?> world() {
        return world;
    }

    public ItemStack itemInHand() {
        return itemInHand;
    }

    public String itemID() {
        return itemID;
    }

    public EquipmentSlot hand() {
        return hand;
    }

    public Player player() {
        return player;
    }
}
