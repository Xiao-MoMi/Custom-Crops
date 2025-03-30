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

package net.momirealms.customcrops.bukkit.integration.custom.craftengine_r1;

import net.momirealms.craftengine.bukkit.api.event.*;
import net.momirealms.craftengine.core.entity.player.InteractionHand;
import net.momirealms.customcrops.api.core.AbstractCustomEventListener;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import net.momirealms.customcrops.api.util.DummyCancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;

public class CraftEngineListener extends AbstractCustomEventListener {

    public CraftEngineListener(AbstractItemManager itemManager) {
        super(itemManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractFurniture(FurnitureInteractEvent event) {
        EquipmentSlot slot = event.hand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        itemManager.handlePlayerInteractFurniture(
                event.getPlayer(),
                event.furniture().location(),
                event.furniture().id().toString(),
                slot,
                event.getPlayer().getInventory().getItem(slot),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(CustomBlockInteractEvent event) {
        if (event.action() == CustomBlockInteractEvent.Action.RIGHT_CLICK) {
            EquipmentSlot slot = event.hand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
            itemManager.handlePlayerInteractBlock(
                    event.getPlayer(),
                    event.location().getBlock(),
                    event.customBlock().id().toString(),
                    event.clickedFace(),
                    slot,
                    event.getPlayer().getInventory().getItem(slot),
                    event
            );
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakFurniture(FurnitureBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.furniture().baseEntity().getLocation(),
                event.getPlayer().getInventory().getItemInMainHand(),
                event.furniture().id().toString(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.bukkitBlock().getLocation(),
                event.getPlayer().getInventory().getItemInMainHand(),
                event.customBlock().id().toString(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        EquipmentSlot slot = event.hand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.location(),
                event.customBlock().id().toString(),
                slot,
                event.getPlayer().getInventory().getItem(slot),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceFurniture(FurniturePlaceEvent event) {
        EquipmentSlot slot = event.hand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        Player player = event.getPlayer();
        itemManager.handlePlayerPlace(
                player,
                event.location(),
                event.furniture().id().toString(),
                slot,
                event.getPlayer().getInventory().getItem(slot),
                new DummyCancellable()
        );
    }
}
