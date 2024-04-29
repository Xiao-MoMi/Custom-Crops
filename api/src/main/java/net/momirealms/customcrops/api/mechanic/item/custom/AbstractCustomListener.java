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

package net.momirealms.customcrops.api.mechanic.item.custom;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.event.BoneMealDispenseEvent;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.ItemManager;
import net.momirealms.customcrops.api.manager.VersionManager;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.util.EventUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCustomListener implements Listener {

    protected ItemManager itemManager;
    private final HashSet<Material> CUSTOM_MATERIAL = new HashSet<>();

    public AbstractCustomListener(ItemManager itemManager) {
        this.itemManager = itemManager;
        this.CUSTOM_MATERIAL.addAll(
                List.of(
                        Material.NOTE_BLOCK,
                        Material.MUSHROOM_STEM,
                        Material.BROWN_MUSHROOM_BLOCK,
                        Material.RED_MUSHROOM_BLOCK,
                        Material.TRIPWIRE,
                        Material.CHORUS_PLANT,
                        Material.CHORUS_FLOWER,
                        Material.ACACIA_LEAVES,
                        Material.BIRCH_LEAVES,
                        Material.JUNGLE_LEAVES,
                        Material.DARK_OAK_LEAVES,
                        Material.AZALEA_LEAVES,
                        Material.FLOWERING_AZALEA_LEAVES,
                        Material.OAK_LEAVES,
                        Material.SPRUCE_LEAVES,
                        Material.CAVE_VINES,
                        Material.TWISTING_VINES,
                        Material.WEEPING_VINES,
                        Material.KELP,
                        Material.CACTUS
                )
        );
        if (VersionManager.isHigherThan1_19()) {
            this.CUSTOM_MATERIAL.add(
                Material.MANGROVE_LEAVES
            );
        }
        if (VersionManager.isHigherThan1_20()) {
            this.CUSTOM_MATERIAL.add(
                Material.CHERRY_LEAVES
            );
        }
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

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();
        // custom block should be handled by other plugins' events
        if (CUSTOM_MATERIAL.contains(type))
            return;
        this.itemManager.handlePlayerBreakBlock(
                player,
                block,
                type.name(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        // prevent players from placing blocks on entities (crops/sprinklers)
        if (CustomCropsPlugin.get().getWorldManager().getBlockAt(SimpleLocation.of(block.getLocation())).isPresent()) {
            event.setCancelled(true);
            return;
        }
        this.onPlaceBlock(
                event.getPlayer(),
                block,
                event.getBlockPlaced().getType().name(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        String itemID = this.itemManager.getItemID(itemStack);
        Crop.Stage stage = this.itemManager.getCropStageByStageID(itemID);
        if (stage != null) {
            event.setCancelled(true);
            return;
        }

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
    public void onBlockChange(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            SimpleLocation above = SimpleLocation.of(block.getLocation()).add(0,1,0);
            if (CustomCropsPlugin.get().getWorldManager().getBlockAt(above).isPresent()) {
                event.setCancelled(true);
            }
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
            itemManager.handleEntityTramplingBlock(event.getEntity(), block, event);
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

    @EventHandler (ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();
        WateringCan wateringCan = this.itemManager.getWateringCanByItemStack(itemStack);
        if (wateringCan != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        this.itemManager.handleExplosion(event.getEntity(), event.blockList(), event);
    }

    @EventHandler (ignoreCancelled = true)
    public void onExplosion(BlockExplodeEvent event) {
        this.itemManager.handleExplosion(null, event.blockList(), event);
    }

    @EventHandler (ignoreCancelled = true)
    public void onDispenser(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getBlockData() instanceof org.bukkit.block.data.type.Dispenser directional) {
            Block relative = block.getRelative(directional.getFacing());
            Location location = relative.getLocation();
            SimpleLocation simpleLocation = SimpleLocation.of(location);
            Optional<WorldCrop> worldCropOptional = CustomCropsPlugin.get().getWorldManager().getCropAt(simpleLocation);
            if (worldCropOptional.isPresent()) {
                WorldCrop crop = worldCropOptional.get();
                Crop config = crop.getConfig();
                ItemStack itemStack = event.getItem();
                String itemID = itemManager.getItemID(itemStack);
                if (crop.getPoint() < config.getMaxPoints()) {
                    for (BoneMeal boneMeal : config.getBoneMeals()) {
                        if (boneMeal.getItem().equals(itemID)) {
                            if (!boneMeal.isDispenserAllowed()) {
                                return;
                            }
                            // fire the event
                            if (EventUtils.fireAndCheckCancel(new BoneMealDispenseEvent(block, itemStack, location, boneMeal, crop))) {
                                event.setCancelled(true);
                                return;
                            }
                            if (block.getState() instanceof Dispenser dispenser) {
                                event.setCancelled(true);
                                Inventory inventory = dispenser.getInventory();
                                for (ItemStack storage : inventory.getStorageContents()) {
                                    if (storage == null) continue;
                                    String id = itemManager.getItemID(storage);
                                    if (id.equals(itemID)) {
                                        storage.setAmount(storage.getAmount() - 1);
                                        boneMeal.trigger(new State(null, itemStack, location));
                                        CustomCropsPlugin.get().getWorldManager().addPointToCrop(config, boneMeal.getPoint(), simpleLocation);
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    public void onPlaceBlock(Player player, Block block, String blockID, Cancellable event) {
        if (player == null) return;
        this.itemManager.handlePlayerPlaceBlock(player, block, blockID, event);
    }

    public void onBreakFurniture(Player player, Location location, String id, Cancellable event) {
        if (player == null) return;
        this.itemManager.handlePlayerBreakFurniture(player, location, id, event);
    }

    public void onPlaceFurniture(Player player, Location location, String id, Cancellable event) {
        if (player == null) return;
        this.itemManager.handlePlayerPlaceFurniture(player, location, id, event);
    }

    public void onInteractFurniture(Player player, Location location, String id, @Nullable Entity baseEntity, Cancellable event) {
        if (player == null) return;
        this.itemManager.handlePlayerInteractFurniture(player, location, id, baseEntity, event);
    }
}
