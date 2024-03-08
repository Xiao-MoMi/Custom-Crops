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

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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

    @EventHandler (ignoreCancelled = false)
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

    @EventHandler (ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        String itemID = this.itemManager.getItemID(itemStack);
        Sprinkler sprinkler = this.itemManager.getSprinklerBy3DItemID(itemID);
        if (sprinkler != null) {
            ItemStack newItem = this.itemManager.getItemStack(null, sprinkler.get2DItemID());
            if (newItem != null) {
                newItem.setAmount(itemStack.getAmount());
                item.setItemStack(newItem);
            }
            return;
        }

        Pot pot = this.itemManager.getPotByBlockID(itemID);
        if (pot != null) {
            ItemStack newItem = this.itemManager.getItemStack(null, pot.getDryItem());
            if (newItem != null) {
                newItem.setAmount(itemStack.getAmount());
                item.setItemStack(newItem);
            }
            return;
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onTrampling(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND && event.getTo() == Material.DIRT) {
            if (ConfigManager.preventTrampling()) {
                event.setCancelled(true);
                return;
            }
            itemManager.handleEntityBreakBlock(event.getEntity(), block, event);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onMoistureChange(MoistureChangeEvent event) {
        if (ConfigManager.disableMoisture())
            event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        WorldManager manager = CustomCropsPlugin.get().getWorldManager();
        for (Block block : event.getBlocks()) {
            if (manager.getBlockAt(SimpleLocation.of(block.getLocation())).isPresent()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        WorldManager manager = CustomCropsPlugin.get().getWorldManager();
        for (Block block : event.getBlocks()) {
            if (manager.getBlockAt(SimpleLocation.of(block.getLocation())).isPresent()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public void onPlaceBlock(Player player, Block block, String blockID, Cancellable event) {
        this.itemManager.handlePlayerPlaceBlock(player, block, blockID, event);
    }

    public void onBreakFurniture(Player player, Location location, String id, Cancellable event) {
        this.itemManager.handlePlayerBreakFurniture(player, location, id, event);
    }

    public void onPlaceFurniture(Player player, Location location, String id, Cancellable event) {
        this.itemManager.handlePlayerPlaceFurniture(player, location, id, event);
    }

    public void onInteractFurniture(Player player, Location location, String id, @Nullable Entity baseEntity, Cancellable event) {
        this.itemManager.handlePlayerInteractFurniture(player, location, id, baseEntity, event);
    }
}
