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

package net.momirealms.customcrops.mechanic.item.custom;

import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCustomListener implements Listener {

    protected ItemManagerImpl itemManager;

    public AbstractCustomListener(ItemManagerImpl itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        this.itemManager.handlePlayerInteractBlock(
                player,
                event.getClickedBlock(),
                event.getBlockFace(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractAir(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        Player player = event.getPlayer();
        this.itemManager.handlePlayerInteractAir(
                player,
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        this.itemManager.handlePlayerBreakBlock(
                player,
                event.getBlock(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        onPlaceBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getBlockPlaced().getType().name(),
                event
        );
    }

    public void onPlaceBlock(Player player, Block block, String blockID, Cancellable event) {
        this.itemManager.handlePlayerPlaceBlock(player, block, blockID, event);
    }

    /**
     * CustomCrops only reads necessary data from the event and would not modify it
     */
    public void onBreakFurniture(Player player, Location location, String id, Cancellable event) {
        this.itemManager.handlePlayerBreakFurniture(player, location, id, event);
    }

    /**
     * CustomCrops only reads necessary data from the event and would not modify it
     */
    public void onPlaceFurniture(Player player, Location location, String id, Cancellable event) {
        this.itemManager.handlePlayerPlaceFurniture(player, location, id, event);
    }

    public void onInteractFurniture(Player player, Location location, String id, @Nullable Entity baseEntity, Cancellable event) {
        this.itemManager.handlePlayerInteractFurniture(player, location, id, baseEntity, event);
    }

}
