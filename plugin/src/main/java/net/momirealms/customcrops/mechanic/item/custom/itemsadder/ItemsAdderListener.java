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

package net.momirealms.customcrops.mechanic.item.custom.itemsadder;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.*;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import net.momirealms.customcrops.api.mechanic.item.custom.AbstractCustomListener;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class ItemsAdderListener extends AbstractCustomListener {

    public ItemsAdderListener(ItemManagerImpl itemManager) {
        super(itemManager);
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        this.itemManager.handlePlayerBreakBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getNamespacedID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        super.onPlaceBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getNamespacedID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        Entity entity = event.getBukkitEntity();
        if (entity == null) return;
        // player would be null if furniture is placed with API
        if (event.getPlayer() == null) return;
        super.onPlaceFurniture(
                event.getPlayer(),
                entity.getLocation(),
                event.getNamespacedID(),
                null
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakFurniture(FurnitureBreakEvent event) {
        CustomFurniture customFurniture = event.getFurniture();
        if (customFurniture == null) return;
        Entity entity = customFurniture.getEntity();
        if (entity == null) return;
        super.onBreakFurniture(
                event.getPlayer(),
                entity.getLocation(),
                event.getNamespacedID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractFurniture(FurnitureInteractEvent event) {
        CustomFurniture customFurniture = event.getFurniture();
        if (customFurniture == null) return;
        Entity entity = customFurniture.getEntity();
        if (entity == null) return;
        super.onInteractFurniture(event.getPlayer(),
                entity.getLocation(),
                event.getNamespacedID(),
                entity,
                event
        );
    }
}
