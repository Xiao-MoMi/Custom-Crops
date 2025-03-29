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

package net.momirealms.customcrops.bukkit.integration.custom.itemsadder_r2;

import dev.lone.itemsadder.api.Events.*;
import net.momirealms.customcrops.api.core.AbstractCustomEventListener;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemsAdderListener extends AbstractCustomEventListener {

    private static final Set<Material> IGNORED = new HashSet<>(
            List.of(
                    Material.NOTE_BLOCK,
                    Material.MUSHROOM_STEM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK,
                    Material.TRIPWIRE,
                    Material.CHORUS_PLANT
            )
    );

    @Override
    protected Set<Material> ignoredMaterials() {
        return IGNORED;
    }

    public ItemsAdderListener(AbstractItemManager itemManager) {
        super(itemManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractFurniture(FurnitureInteractEvent event) {
        itemManager.handlePlayerInteractFurniture(
                event.getPlayer(),
                event.getBukkitEntity().getLocation(), event.getNamespacedID(),
                EquipmentSlot.HAND, event.getPlayer().getInventory().getItemInMainHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(CustomBlockInteractEvent event) {
        itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                event.getBlockClicked(),
                event.getNamespacedID(),
                event.getBlockFace(),
                event.getHand(),
                event.getItem(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakFurniture(FurnitureBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBukkitEntity().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getNamespacedID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBlock().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getNamespacedID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getNamespacedID(),
                EquipmentSlot.HAND,
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceFurniture(FurniturePlacedEvent event) {
        Player player = event.getPlayer();
        // ItemsAdder would trigger FurniturePlaceSuccessEvent if the furniture is placed by API
        if (player == null) return;
        itemManager.handlePlayerPlace(
                player,
                event.getBukkitEntity().getLocation(),
                event.getNamespacedID(),
                EquipmentSlot.HAND,
                event.getPlayer().getInventory().getItemInMainHand(),
                event
        );
    }
}
