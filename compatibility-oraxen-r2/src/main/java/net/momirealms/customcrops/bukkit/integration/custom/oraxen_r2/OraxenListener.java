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

package net.momirealms.customcrops.bukkit.integration.custom.oraxen_r2;

import io.th0rgal.oraxen.api.events.custom_block.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.custom_block.noteblock.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.api.events.custom_block.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.api.events.custom_block.stringblock.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.api.events.custom_block.stringblock.OraxenStringBlockInteractEvent;
import io.th0rgal.oraxen.api.events.custom_block.stringblock.OraxenStringBlockPlaceEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurniturePlaceEvent;
import io.th0rgal.oraxen.font.Glyph;
import net.momirealms.customcrops.api.core.AbstractCustomEventListener;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OraxenListener extends AbstractCustomEventListener {

    private static final Set<Material> IGNORED = new HashSet<>(
            List.of(
                    Material.NOTE_BLOCK,
                    Material.TRIPWIRE
            )
    );

    @Override
    protected Set<Material> ignoredMaterials() {
        return IGNORED;
    }

    public OraxenListener(AbstractItemManager itemManager) {
        super(itemManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        itemManager.handlePlayerInteractFurniture(
                event.player(),
                event.baseEntity().getLocation(), event.mechanic().getItemID(),
                event.hand(), event.itemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(OraxenNoteBlockInteractEvent event) {
        itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getMechanic().getItemID(), event.getBlockFace(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(OraxenStringBlockInteractEvent event) {
        itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getMechanic().getItemID(), event.getBlockFace(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBaseEntity().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(OraxenNoteBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBlock().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(OraxenStringBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBlock().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBaseEntity().getLocation(),
                event.getMechanic().getItemID(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(OraxenNoteBlockPlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(OraxenStringBlockPlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }
}
