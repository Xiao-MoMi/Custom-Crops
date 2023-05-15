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

package net.momirealms.customcrops.api.customplugin.oraxen;

import io.th0rgal.oraxen.api.events.*;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import net.momirealms.customcrops.api.customplugin.Handler;
import net.momirealms.customcrops.api.customplugin.PlatformManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class OraxenHandler extends Handler {

    public OraxenHandler(PlatformManager platformManager) {
        super(platformManager);
    }

    @EventHandler
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        platformManager.onBreakNoteBlock(event.getPlayer(), event.getBlock(), event.getMechanic().getItemID(), event);
    }

    @EventHandler
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        platformManager.onBreakTripWire(event.getPlayer(), event.getBlock(), event.getMechanic().getItemID(), event);
    }

    @EventHandler
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        Entity entity = event.getBaseEntity();
        if (entity == null) return;
        switch (entity.getType()) {
            case ITEM_FRAME -> platformManager.onBreakItemFrame(event.getPlayer(), entity, event.getMechanic().getItemID(), event);
            case ITEM_DISPLAY -> platformManager.onBreakItemDisplay(event.getPlayer(), entity, event.getMechanic().getItemID(), event);
        }
    }

    @EventHandler
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        FurnitureMechanic mechanic = event.getMechanic();
        if (mechanic == null) return;
        platformManager.onPlaceFurniture(event.getPlayer(), event.getBaseEntity().getLocation().getBlock().getLocation(), mechanic.getItemID(), event);
    }

    @EventHandler
    public void onPlaceStringBlock(OraxenStringBlockPlaceEvent event) {
        StringBlockMechanic mechanic = event.getMechanic();
        if (mechanic == null) return;
        platformManager.onPlaceBlock(event.getPlayer(), event.getBlock().getLocation(), mechanic.getItemID(), event);
    }

    @EventHandler
    public void onPlaceNoteBlock(OraxenNoteBlockPlaceEvent event) {
        NoteBlockMechanic mechanic = event.getMechanic();
        if (mechanic == null) return;
        platformManager.onPlaceBlock(event.getPlayer(), event.getBlock().getLocation(), mechanic.getItemID(), event);
    }

    @EventHandler
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        platformManager.onInteractFurniture(event.getPlayer(), event.getBaseEntity(), event.getMechanic().getItemID(), event);
    }
}
