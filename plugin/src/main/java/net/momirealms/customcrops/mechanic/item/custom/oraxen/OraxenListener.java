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

package net.momirealms.customcrops.mechanic.item.custom.oraxen;

import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurniturePlaceEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockPlaceEvent;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import net.momirealms.customcrops.mechanic.item.custom.AbstractCustomListener;
import org.bukkit.event.EventHandler;

public class OraxenListener extends AbstractCustomListener {

    public OraxenListener(ItemManagerImpl itemManager) {
        super(itemManager);
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceCustomBlock(OraxenNoteBlockPlaceEvent event) {
        super.onPlaceBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceCustomBlock(OraxenStringBlockPlaceEvent event) {
        super.onPlaceBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        super.onPlaceFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        super.onBreakFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        super.onInteractFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event.getBaseEntity(),
                event
        );
    }
}
