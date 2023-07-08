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

import dev.lone.itemsadder.api.Events.*;
import net.momirealms.customcrops.customplugin.Handler;
import net.momirealms.customcrops.customplugin.PlatformManager;
import org.bukkit.event.EventHandler;

public class ItemsAdderHandler extends Handler {

    public ItemsAdderHandler(PlatformManager platformManager) {
        super(platformManager);
    }

    @EventHandler
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        platformManager.onBreakCustomBlock(event.getPlayer(), event.getBlock().getLocation(), event.getNamespacedID(), event);
    }

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event) {
        platformManager.onBreakFurniture(event.getPlayer(), event.getBukkitEntity(), event.getNamespacedID(), event);
    }

    @EventHandler
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        platformManager.onPlaceFurniture(event.getPlayer(), event.getBukkitEntity().getLocation().getBlock().getLocation(), event.getNamespacedID(), null);
    }

    @EventHandler
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        platformManager.onPlaceCustomBlock(event.getPlayer(), event.getBlock().getLocation(), event.getNamespacedID(), event);
    }

    @EventHandler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        platformManager.onInteractFurniture(event.getPlayer(), event.getBukkitEntity(), event.getNamespacedID(), event);
    }
}
