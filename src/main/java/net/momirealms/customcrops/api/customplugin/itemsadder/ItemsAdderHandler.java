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

package net.momirealms.customcrops.api.customplugin.itemsadder;

import dev.lone.itemsadder.api.Events.*;
import net.momirealms.customcrops.api.customplugin.Handler;
import net.momirealms.customcrops.api.customplugin.PlatformManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class ItemsAdderHandler extends Handler {

    public ItemsAdderHandler(PlatformManager platformManager) {
        super(platformManager);
    }

    @EventHandler
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        Block block = event.getBlock();
        switch (block.getType()) {
            case NOTE_BLOCK -> platformManager.onBreakNoteBlock(event.getPlayer(), event.getBlock(), event.getNamespacedID(), event);
            case TRIPWIRE -> platformManager.onBreakTripWire(event.getPlayer(), event.getBlock(), event.getNamespacedID(), event);
        }
    }

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event) {
        Entity entity = event.getBukkitEntity();
        switch (entity.getType()) {
            case ITEM_FRAME -> platformManager.onBreakItemFrame(event.getPlayer(), entity, event.getNamespacedID(), event);
            case ITEM_DISPLAY -> platformManager.onBreakItemDisplay(event.getPlayer(), entity, event.getNamespacedID(), event);
        }
    }

    @EventHandler
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        platformManager.onPlaceFurniture(event.getPlayer(), event.getBukkitEntity().getLocation().getBlock().getLocation(), event.getNamespacedID(), null);
    }

    @EventHandler
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        platformManager.onPlaceBlock(event.getPlayer(), event.getBlock().getLocation(), event.getNamespacedID(), event);
    }

    @EventHandler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        platformManager.onInteractFurniture(event.getPlayer(), event.getBukkitEntity(), event.getNamespacedID(), event);
    }
}
