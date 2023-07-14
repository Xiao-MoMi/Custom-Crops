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

package net.momirealms.customcrops.customplugin;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.*;
import net.momirealms.customcrops.api.object.BoneMeal;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.InteractCrop;
import net.momirealms.customcrops.api.object.action.Action;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.crop.StageConfig;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.fill.PositiveFillMethod;
import net.momirealms.customcrops.api.object.hologram.FertilizerHologram;
import net.momirealms.customcrops.api.object.hologram.WaterAmountHologram;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.pot.PotConfig;
import net.momirealms.customcrops.api.object.requirement.CurrentState;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import net.momirealms.customcrops.api.object.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.object.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.customplugin.itemsadder.ItemsAdderHandler;
import net.momirealms.customcrops.customplugin.oraxen.OraxenHandler;
import net.momirealms.customcrops.util.AdventureUtils;
import net.momirealms.customcrops.util.RotationUtils;
import net.momirealms.protectionlib.ProtectionLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlatformManager extends Function {

    private final CustomCrops plugin;
    private final Handler handler;
    private static final HashSet<Material> REPLACEABLE = new HashSet<>(Arrays.asList(Material.SNOW, Material.VINE, Material.GRASS, Material.TALL_GRASS, Material.SEAGRASS, Material.FERN, Material.LARGE_FERN, Material.AIR));

    public PlatformManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.handler = switch (plugin.getPlatform()) {
            case ItemsAdder -> new ItemsAdderHandler(this);
            case Oraxen -> new OraxenHandler(this);
        };
    }

    @Override
    public void load() {
        this.handler.load();
    }

    @Override
    public void unload() {
        this.handler.unload();
    }

    public void onPlaceVanillaBlock(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        onPlaceVanilla(event.getPlayer(), block.getLocation(), block.getType().name(), event);
    }

    public void onBreakVanillaBlock(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        onBreakVanilla(event.getPlayer(), block.getLocation(), block.getType().name(), event);
    }

    public void onPlaceFurniture(Player player, Location location, String id, Cancellable event) {
        if (event != null && event.isCancelled()) return;
        onPlaceCustom(player, location, id, event);
    }

    public void onBreakFurniture(Player player, Entity entity, String id, Cancellable event) {
        if (event.isCancelled()) return;
        onBreakCustom(player, entity.getLocation().getBlock().getLocation(), id, event);
    }

    public void onPlaceCustomBlock(Player player, Location location, String id, Cancellable event) {
        if (event.isCancelled()) return;
        onPlaceCustom(player, location, id, event);
    }

    public void onBreakCustomBlock(Player player, Location location, String id, Cancellable event) {
        if (event.isCancelled()) return;
        onBreakCustom(player, location, id, event);
    }

    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR) {
            onInteractAir(event.getPlayer());
        } else if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            String id = plugin.getPlatformInterface().getBlockID(block);
            assert block != null;
            if (ConfigManager.enableCorruptionFixer && onInteractBrokenPot(id, block.getLocation())) {
                return;
            }
            onInteractSomething(event.getPlayer(), block.getLocation(), id, event.getBlockFace(), event);
        }
    }

    public void onInteractFurniture(Player player, Entity entity, String id, Cancellable event) {
        if (event.isCancelled()) return;
        onInteractSomething(player, entity.getLocation().getBlock().getLocation(), id, null, event);
    }

    public void onInteractAir(Player player) {
        ItemStack item_in_hand = player.getInventory().getItemInMainHand();
        String id = plugin.getPlatformInterface().getItemStackID(item_in_hand);
        onInteractWithWateringCan(player, id, item_in_hand, null, null);
    }

    public void onBreakCustom(Player player, Location location, String id, Cancellable event) {

        if (onBreakGlass(player, id, location, event)) {
            return;
        }

        if (onBreakPot(player, id, location, event)) {
            return;
        }

        if (onBreakCrop(player, id, location, event)) {
            return;
        }

        if (onBreakSprinkler(player, id, location, event)) {
            return;
        }

        if (onBreakScarecrow(player, id, location, event)) {
            return;
        }
    }

    public void onBreakVanilla(Player player, Location location, String id, Cancellable event) {

        if (onBreakGlass(player, id, location, event)) {
            return;
        }

        if (onBreakPot(player, id, location, event)) {
            return;
        }

        if (id.equals("NOTE_BLOCK")) {
            SimpleLocation potLoc = SimpleLocation.getByBukkitLocation(location);
            plugin.getWorldDataManager().removeCorrupted(potLoc);
            plugin.getWorldDataManager().removePotData(potLoc);
        }
    }

    public void onPlaceCustom(Player player, Location location, String id, @Nullable Cancellable event) {

        if (onPlaceGlass(player, id, location, event)) {
            return;
        }

        if (onPlacePot(player, id, location, event)) {
            return;
        }

        if (onPlaceScarecrow(player, id, location, event)) {
            return;
        }
    }

    public void onPlaceVanilla(Player player, Location location, String id, @Nullable Cancellable event) {

        if (onPlaceGlass(player, id, location, event)) {
            return;
        }

        if (onPlacePot(player, id, location, event)) {
            return;
        }
    }

    void onInteractSomething(Player player, Location location, String id, @Nullable BlockFace blockFace, Cancellable event) {

        if (!plugin.getWorldDataManager().isWorldAllowed(location.getWorld())) {
            return;
        }

        ItemStack item_in_hand = player.getInventory().getItemInMainHand();
        String item_in_hand_id = plugin.getPlatformInterface().getItemStackID(item_in_hand);

        if (onInteractCrop(player, id, location, item_in_hand, item_in_hand_id, event)) {
            return;
        }

        if (onInteractWithSprinkler(player, location, item_in_hand, item_in_hand_id, blockFace)) {
            return;
        }

        if (onInteractSprinkler(player, id, location, item_in_hand, item_in_hand_id, event)) {
            return;
        }

        if (onInteractPot(player, id, location, item_in_hand, item_in_hand_id, event)) {
            return;
        }

        if (onInteractWithWateringCan(player, item_in_hand_id, item_in_hand, id, location)) {
            return;
        }
    }

    /**
     * Fix a pot if CustomCrops detected the location seems to be corrupted
     */
    private boolean onInteractBrokenPot(String id, Location location) {
        if (!id.equals("NOTE_BLOCK")) {
            return false;
        }
        SimpleLocation simpleLocation = SimpleLocation.getByBukkitLocation(location);
        String replacer;
        String potKey = plugin.getWorldDataManager().removeCorrupted(simpleLocation);
        Pot pot = plugin.getWorldDataManager().getPotData(simpleLocation);
        if (pot != null) {
            replacer = pot.isWet() ? pot.getConfig().getWetPot(pot.getFertilizer()) : pot.getConfig().getDryPot(pot.getFertilizer());
        } else {
            if (potKey == null) {
                return false;
            }
            PotConfig potConfig = plugin.getPotManager().getPotConfig(potKey);
            if (potConfig == null) {
                return true;
            }
            replacer = potConfig.getDryPot(null);
        }
        plugin.getPlatformInterface().placeNoteBlock(location, replacer);
        furtherFix(location, 1);
        return true;
    }

    /**
     * Fix the pots according to the range
     * The scope of detection is to some extent determined by the scope of corruption
     */
    public void furtherFix(Location location, int range) {
        if (range >= ConfigManager.fixRange) return;
        List<Location> locations = new ArrayList<>();
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                if (i == -range || i == range || j == range || j == -range) {
                    locations.add(location.clone().add(i, 0, j));
                }
            }
        }
        int success = 0;
        for (Location check : locations) {
            SimpleLocation simpleLocation = SimpleLocation.getByBukkitLocation(check);
            String id = plugin.getPlatformInterface().getBlockID(check.getBlock());
            String potKey = plugin.getWorldDataManager().removeCorrupted(simpleLocation);
            if (!id.equals("NOTE_BLOCK")) {
                continue;
            }

            String replacer;
            Pot pot = plugin.getWorldDataManager().getPotData(simpleLocation);
            if (pot != null) {
                replacer = pot.isWet() ? pot.getConfig().getWetPot(pot.getFertilizer()) : pot.getConfig().getDryPot(pot.getFertilizer());
            } else {
                if (potKey == null) {
                    continue;
                }
                PotConfig potConfig = plugin.getPotManager().getPotConfig(potKey);
                if (potConfig == null) {
                    continue;
                }
                replacer = potConfig.getDryPot(null);
            }
            plugin.getPlatformInterface().placeNoteBlock(check, replacer);
            success++;
        }

        if (success != 0) {
            furtherFix(location, range + 1);
        }
    }

    public boolean onBreakGlass(Player player, String id, Location location, Cancellable event) {
        if (!id.equals(ConfigManager.greenhouseBlock)) {
            return false;
        }

        GreenhouseGlassBreakEvent greenhouseGlassBreakEvent = new GreenhouseGlassBreakEvent(player, location);
        if (greenhouseGlassBreakEvent.isCancelled()) {
            event.setCancelled(true);
            return true;
        }

        plugin.getWorldDataManager().removeGreenhouse(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    public boolean onPlaceGlass(Player player, String id, Location location, Cancellable event) {
        if (!id.equals(ConfigManager.greenhouseBlock)) {
            return false;
        }

        GreenhouseGlassPlaceEvent greenhouseGlassPlaceEvent = new GreenhouseGlassPlaceEvent(player, location);
        Bukkit.getPluginManager().callEvent(greenhouseGlassPlaceEvent);
        if (greenhouseGlassPlaceEvent.isCancelled()) {
            if (event != null) event.setCancelled(true);
            return true;
        }

        plugin.getWorldDataManager().addGreenhouse(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    public boolean onBreakScarecrow(Player player, String id, Location location, Cancellable event) {
        if (!id.equals(ConfigManager.scarecrow)) {
            return false;
        }

        ScarecrowBreakEvent scarecrowBreakEvent = new ScarecrowBreakEvent(player, location);
        Bukkit.getPluginManager().callEvent(scarecrowBreakEvent);
        if (scarecrowBreakEvent.isCancelled()) {
            event.setCancelled(true);
            return true;
        }

        plugin.getWorldDataManager().removeScarecrow(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    public boolean onPlaceScarecrow(Player player, String id, Location location, Cancellable event) {
        if (!id.equals(ConfigManager.scarecrow)) {
            return false;
        }

        ScarecrowPlaceEvent scarecrowPlaceEvent = new ScarecrowPlaceEvent(player, location);
        Bukkit.getPluginManager().callEvent(scarecrowPlaceEvent);
        if (scarecrowPlaceEvent.isCancelled()) {
            if (event != null) event.setCancelled(true);
            return true;
        }

        plugin.getWorldDataManager().addScarecrow(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    private boolean onPlacePot(Player player, String id, Location location, Cancellable event) {
        PotConfig potConfig = plugin.getPotManager().getPotConfigByBlockID(id);
        if (potConfig == null) return false;

        PotPlaceEvent potPlaceEvent = new PotPlaceEvent(player, location, potConfig.getKey());
        Bukkit.getPluginManager().callEvent(potPlaceEvent);
        if (potPlaceEvent.isCancelled()) {
            if (event != null) event.setCancelled(true);
        }

        return true;
    }

    public boolean onInteractSprinkler(Player player, String id, Location location, ItemStack item_in_hand, String item_in_hand_id, Cancellable event) {
        SprinklerConfig sprinklerConfig = plugin.getSprinklerManager().getConfigByItemID(id);
        if (sprinklerConfig == null) {
            return false;
        }

        if (!ProtectionLib.canPlace(player, location)) {
            return true;
        }

        SprinklerInteractEvent sprinklerInteractEvent = new SprinklerInteractEvent(player, item_in_hand, location, sprinklerConfig.getKey());
        Bukkit.getPluginManager().callEvent(sprinklerInteractEvent);
        if (sprinklerInteractEvent.isCancelled()) {
            event.setCancelled(true);
            return true;
        }

        outer: {
            // water
            PassiveFillMethod[] passiveFillMethods = sprinklerConfig.getPassiveFillMethods();
            for (PassiveFillMethod passiveFillMethod : passiveFillMethods) {
                if (passiveFillMethod.isRightItem(item_in_hand_id)) {

                    SprinklerFillEvent sprinklerFillEvent = new SprinklerFillEvent(player, sprinklerConfig.getKey(), item_in_hand, passiveFillMethod.getAmount(), location);
                    Bukkit.getPluginManager().callEvent(sprinklerFillEvent);
                    if (sprinklerFillEvent.isCancelled()) {
                        return true;
                    }

                    event.setCancelled(true);
                    doPassiveFillAction(player, item_in_hand, passiveFillMethod, location.clone().add(0,0.2,0));
                    plugin.getWorldDataManager().addWaterToSprinkler(SimpleLocation.getByBukkitLocation(location), sprinklerFillEvent.getWater(), sprinklerConfig);
                    break outer;
                }
            }

            WateringCanConfig wateringCanConfig = plugin.getWateringCanManager().getConfigByItemID(item_in_hand_id);
            if (wateringCanConfig != null) {
                String[] sprinkler_whitelist = wateringCanConfig.getSprinklerWhitelist();
                if (sprinkler_whitelist != null) {
                    inner: {
                        for (String sprinkler_allowed : sprinkler_whitelist) {
                            if (sprinkler_allowed.equals(plugin.getSprinklerManager().getConfigKeyByItemID(id))) {
                                break inner;
                            }
                        }
                        return true;
                    }
                }
                int current_water = plugin.getWateringCanManager().getCurrentWater(item_in_hand);
                if (current_water <= 0) return true;

                SprinklerFillEvent sprinklerFillEvent = new SprinklerFillEvent(player, sprinklerConfig.getKey(), item_in_hand, 1, location);
                Bukkit.getPluginManager().callEvent(sprinklerFillEvent);
                if (sprinklerFillEvent.isCancelled()) {
                    return true;
                }

                current_water--;
                if (wateringCanConfig.hasActionBar()) {
                    AdventureUtils.playerActionbar(player, wateringCanConfig.getActionBarMsg(current_water));
                }
                if (wateringCanConfig.getSound() != null) {
                    AdventureUtils.playerSound(player, wateringCanConfig.getSound());
                }
                if (wateringCanConfig.getParticle() != null) {
                    location.getWorld().spawnParticle(wateringCanConfig.getParticle(), location.clone().add(0.5,0.4, 0.5),5,0.3,0.1,0.3);
                }

                plugin.getWateringCanManager().setWater(item_in_hand, current_water, wateringCanConfig);
                plugin.getWorldDataManager().addWaterToSprinkler(SimpleLocation.getByBukkitLocation(location), 1, sprinklerConfig);
                break outer;
            }
        }

        Sprinkler sprinkler = plugin.getWorldDataManager().getSprinklerData(SimpleLocation.getByBukkitLocation(location));
        WaterAmountHologram waterAmountHologram = sprinklerConfig.getSprinklerHologram();
        if (waterAmountHologram != null) {
            String content;
            if (sprinkler != null) {
                content = waterAmountHologram.getContent(sprinkler.getWater(), sprinklerConfig.getStorage());
            } else {
                content = waterAmountHologram.getContent(0, sprinklerConfig.getStorage());
            }
            plugin.getHologramManager().showHologram(player,
                    location.clone().add(0.5, waterAmountHologram.getOffset(),0.5),
                    AdventureUtils.getComponentFromMiniMessage(content),
                    waterAmountHologram.getDuration() * 1000,
                    waterAmountHologram.getMode(),
                    waterAmountHologram.getTextDisplayMeta()
            );
        }
        return true;
    }

    private void doPassiveFillAction(Player player, ItemStack item_in_hand, PassiveFillMethod passiveFillMethod, Location location) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            item_in_hand.setAmount(item_in_hand.getAmount() - 1);
            ItemStack returned = passiveFillMethod.getReturnedItemStack();
            if (returned != null) {
                player.getInventory().addItem(returned);
            }
        }
        if (passiveFillMethod.getSound() != null) {
            AdventureUtils.playerSound(player, passiveFillMethod.getSound());
        }
        if (passiveFillMethod.getParticle() != null) {
            location.getWorld().spawnParticle(passiveFillMethod.getParticle(), location.clone().add(0.5,0.3, 0.5),5,0.3,0.1,0.3);
        }
    }

    public boolean onInteractWithSprinkler(Player player, Location location, ItemStack item_in_hand, String item_in_hand_id, @Nullable BlockFace blockFace) {
        SprinklerConfig sprinklerConfig = plugin.getSprinklerManager().getConfigByItemID(item_in_hand_id);
        if (sprinklerConfig == null) {
            return false;
        }

        if (blockFace != BlockFace.UP || REPLACEABLE.contains(location.getBlock().getType())) {
            return true;
        }

        if (!ProtectionLib.canPlace(player, location)) {
            return true;
        }

        Location sprinkler_loc = location.clone().add(0,1,0);
        if (plugin.getPlatformInterface().detectAnyThing(sprinkler_loc)) return true;

        SprinklerPlaceEvent sprinklerPlaceEvent = new SprinklerPlaceEvent(player, item_in_hand, sprinkler_loc, sprinklerConfig.getKey());
        Bukkit.getPluginManager().callEvent(sprinklerPlaceEvent);
        if (sprinklerPlaceEvent.isCancelled()) {
            return true;
        }

        if (player.getGameMode() != GameMode.CREATIVE) item_in_hand.setAmount(item_in_hand.getAmount() - 1);
        CustomCrops.getInstance().getPlatformInterface().placeCustomItem(sprinkler_loc, sprinklerConfig.getThreeD(), sprinklerConfig.getItemMode());
        if (sprinklerConfig.getSound() != null) {
            AdventureUtils.playerSound(player, sprinklerConfig.getSound());
        }
        return true;
    }

    public boolean onInteractCrop(Player player, String id, Location location, ItemStack item_in_hand, String item_in_hand_id, Cancellable event) {
        CropConfig cropConfig = plugin.getCropManager().getCropConfigByStage(id);
        if (cropConfig == null) {
            return false;
        }

        StageConfig stageConfig = plugin.getCropManager().getStageConfig(id);
        if (stageConfig == null) {
            return true;
        }

        if (!ProtectionLib.canBreak(player, location)) {
            return true;
        }

        CropInteractEvent cropInteractEvent = new CropInteractEvent(player, item_in_hand, location, id, cropConfig.getKey());
        Bukkit.getPluginManager().callEvent(cropInteractEvent);
        if (cropInteractEvent.isCancelled()) {
            event.setCancelled(true);
            return true;
        }

        if (item_in_hand_id.equals("AIR")) {
            InteractCrop interactCrop = stageConfig.getInteractByHand();
            if (interactCrop != null) {
                Requirement[] requirements = interactCrop.getRequirements();
                if (requirements != null) {
                    CurrentState currentState = new CurrentState(location, player);
                    for (Requirement requirement : requirements) {
                        if (!requirement.isConditionMet(currentState)) {
                            return true;
                        }
                    }
                }
                for (Action action : interactCrop.getActions()) {
                    action.doOn(player, SimpleLocation.getByBukkitLocation(location), cropConfig.getCropMode());
                }
            }
            return true;
        }

        Location bottomLoc = location.clone().subtract(0, 1, 0);
        String bottomBlockID = plugin.getPlatformInterface().getBlockID(bottomLoc.getBlock());
        String pot_id = plugin.getPotManager().getPotKeyByBlockID(bottomBlockID);
        if (pot_id != null) {
            PotConfig potConfig = plugin.getPotManager().getPotConfig(pot_id);
            if (potConfig != null) {
                PassiveFillMethod[] passiveFillMethods = potConfig.getPassiveFillMethods();
                if (passiveFillMethods != null) {
                    for (PassiveFillMethod passiveFillMethod : passiveFillMethods) {
                        if (passiveFillMethod.isRightItem(item_in_hand_id)) {

                            PotWaterEvent potWaterEvent = new PotWaterEvent(player, item_in_hand, passiveFillMethod.getAmount(), bottomLoc);
                            Bukkit.getPluginManager().callEvent(potWaterEvent);
                            if (potWaterEvent.isCancelled()) {
                                return true;
                            }

                            event.setCancelled(true);
                            doPassiveFillAction(player, item_in_hand, passiveFillMethod, location);
                            plugin.getWorldDataManager().addWaterToPot(SimpleLocation.getByBukkitLocation(bottomLoc), potWaterEvent.getWater(), pot_id);
                            return true;
                        }
                    }
                }

                WateringCanConfig wateringCanConfig = plugin.getWateringCanManager().getConfigByItemID(item_in_hand_id);
                if (wateringCanConfig != null) {
                    String[] pot_whitelist = wateringCanConfig.getPotWhitelist();
                    if (pot_whitelist != null) {
                        inner: {
                            for (String pot : pot_whitelist) {
                                if (pot.equals(pot_id)) {
                                    break inner;
                                }
                            }
                            return true;
                        }
                    }

                    int current_water = plugin.getWateringCanManager().getCurrentWater(item_in_hand);
                    if (current_water <= 0) return true;

                    PotWaterEvent potWaterEvent = new PotWaterEvent(player, item_in_hand, 1, bottomLoc);
                    Bukkit.getPluginManager().callEvent(potWaterEvent);
                    if (potWaterEvent.isCancelled()) {
                        return true;
                    }

                    current_water--;
                    this.waterPot(wateringCanConfig.getWidth(), wateringCanConfig.getLength(), bottomLoc, player.getLocation().getYaw(), pot_id, wateringCanConfig.getParticle(), potWaterEvent.getWater());
                    this.doWateringCanActions(player, item_in_hand, wateringCanConfig, current_water);
                    return true;
                }
            }
        }

        BoneMeal[] boneMeals = cropConfig.getBoneMeals();
        if (boneMeals != null) {
            for (BoneMeal boneMeal : boneMeals) {
                if (boneMeal.isRightItem(item_in_hand_id)) {
                    if (plugin.getWorldDataManager().addCropPointAt(SimpleLocation.getByBukkitLocation(location), boneMeal.getPoint())) {
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            item_in_hand.setAmount(item_in_hand.getAmount() - 1);
                            if (boneMeal.getReturned() != null) {
                                player.getInventory().addItem(boneMeal.getReturned());
                            }
                        }
                        if (boneMeal.getParticle() != null) {
                            location.getWorld().spawnParticle(boneMeal.getParticle(), location.clone().add(0.5,0.5, 0.5),3,0.4,0.4,0.4);
                        }
                        if (boneMeal.getSound() != null) {
                            AdventureUtils.playerSound(player, boneMeal.getSound());
                        }
                    }
                    return true;
                }
            }
        }

        InteractCrop[] interactActions = stageConfig.getInteractCropWithItem();
        if (interactActions != null) {
            outer:
            for (InteractCrop interactCrop : interactActions) {
                if (interactCrop.isRightItem(item_in_hand_id)) {
                    Requirement[] requirements = interactCrop.getRequirements();
                    if (requirements != null) {
                        CurrentState currentState = new CurrentState(location, player);
                        for (Requirement requirement : requirements) {
                            if (!requirement.isConditionMet(currentState)) {
                                continue outer;
                            }
                        }
                    }
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        if (interactCrop.isConsumed()) {
                            item_in_hand.setAmount(item_in_hand.getAmount() - 1);
                        }
                        if (interactCrop.getReturned() != null) {
                            player.getInventory().addItem(interactCrop.getReturned());
                        }
                    }
                    Action[] inAc = interactCrop.getActions();
                    if (inAc != null) {
                        for (Action action : inAc) {
                            action.doOn(player, SimpleLocation.getByBukkitLocation(location), cropConfig.getCropMode());
                        }
                    }
                    return true;
                }
            }
        }
        return true;
    }

    public boolean onInteractPot(Player player, String id, Location location, ItemStack item_in_hand, String item_in_hand_id, Cancellable event) {
        String pot_id = plugin.getPotManager().getPotKeyByBlockID(id);
        if (pot_id == null) {
            return false;
        }

        PotConfig potConfig = plugin.getPotManager().getPotConfig(pot_id);
        if (potConfig == null) {
            return false;
        }

        if (!ProtectionLib.canPlace(player, location)) {
            return true;
        }

        PotInteractEvent potInteractEvent = new PotInteractEvent(player, item_in_hand, location, pot_id);
        Bukkit.getPluginManager().callEvent(potInteractEvent);
        if (potInteractEvent.isCancelled()) {
            return true;
        }

        outer: {
            // plant
            CropConfig cropConfig = plugin.getCropManager().getCropConfigBySeed(item_in_hand_id);
            if (cropConfig != null) {
                String[] pot_whitelist = cropConfig.getPotWhitelist();
                inner: {
                    for (String bottom_block : pot_whitelist) {
                        if (bottom_block.equals(pot_id)) {
                            break inner;
                        }
                    }
                    AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.unsuitablePot);
                    return true;
                }

                Location crop_loc = location.clone().add(0,1,0);
                Requirement[] requirements = cropConfig.getPlantRequirements();
                if (requirements != null) {
                    CurrentState currentState = new CurrentState(crop_loc, player);
                    for (Requirement requirement : requirements) {
                        if (!requirement.isConditionMet(currentState)) {
                            return true;
                        }
                    }
                }

                if (plugin.getPlatformInterface().detectAnyThing(crop_loc)) return true;
                if (ConfigManager.enableLimitation && plugin.getWorldDataManager().getChunkCropAmount(SimpleLocation.getByBukkitLocation(crop_loc)) >= plugin.getConfigManager().getCropLimit(location.getWorld().getName())) {
                    AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.reachChunkLimit);
                    return true;
                }

                String crop_model = Objects.requireNonNull(cropConfig.getStageConfig(0)).getModel();
                CropPlantEvent cropPlantEvent = new CropPlantEvent(player, item_in_hand, location, cropConfig.getKey(), 0, crop_model);
                Bukkit.getPluginManager().callEvent(cropPlantEvent);
                if (cropPlantEvent.isCancelled()) {
                    return true;
                }

                Action[] plantActions = cropConfig.getPlantActions();
                if (plantActions != null) {
                    for (Action action : plantActions) {
                        action.doOn(player, SimpleLocation.getByBukkitLocation(crop_loc), cropConfig.getCropMode());
                    }
                }

                if (player.getGameMode() != GameMode.CREATIVE) item_in_hand.setAmount(item_in_hand.getAmount() - 1);
                player.swingMainHand();
                switch (cropConfig.getCropMode()) {
                    case ITEM_DISPLAY -> {
                        ItemDisplay itemDisplay = CustomCrops.getInstance().getPlatformInterface().placeItemDisplay(crop_loc, cropPlantEvent.getCropModel());
                        if (itemDisplay != null && cropConfig.isRotationEnabled()) itemDisplay.setRotation(RotationUtils.getRandomFloatRotation(), itemDisplay.getLocation().getPitch());
                    }
                    case ITEM_FRAME -> {
                        ItemFrame itemFrame = CustomCrops.getInstance().getPlatformInterface().placeItemFrame(crop_loc, cropPlantEvent.getCropModel());
                        if (itemFrame != null && cropConfig.isRotationEnabled()) itemFrame.setRotation(RotationUtils.getRandomRotation());
                    }
                    case TRIPWIRE -> CustomCrops.getInstance().getPlatformInterface().placeTripWire(crop_loc, cropPlantEvent.getCropModel());
                }

                plugin.getWorldDataManager().addCropData(SimpleLocation.getByBukkitLocation(crop_loc), new GrowingCrop(cropConfig.getKey(), cropPlantEvent.getPoint()), true);
                return true;
            }

            // water
            PassiveFillMethod[] passiveFillMethods = potConfig.getPassiveFillMethods();
            if (passiveFillMethods != null) {
                for (PassiveFillMethod passiveFillMethod : passiveFillMethods) {
                    if (passiveFillMethod.isRightItem(item_in_hand_id)) {

                        PotWaterEvent potWaterEvent = new PotWaterEvent(player, item_in_hand, passiveFillMethod.getAmount(), location);
                        Bukkit.getPluginManager().callEvent(potWaterEvent);
                        if (potWaterEvent.isCancelled()) {
                            return true;
                        }

                        event.setCancelled(true);
                        doPassiveFillAction(player, item_in_hand, passiveFillMethod, location.clone().add(0,1,0));
                        plugin.getWorldDataManager().addWaterToPot(SimpleLocation.getByBukkitLocation(location), potWaterEvent.getWater(), pot_id);
                        break outer;
                    }
                }
            }

            // use watering can
            WateringCanConfig wateringCanConfig = plugin.getWateringCanManager().getConfigByItemID(item_in_hand_id);
            if (wateringCanConfig != null) {
                String[] pot_whitelist = wateringCanConfig.getPotWhitelist();
                if (pot_whitelist != null) {
                    inner: {
                        for (String pot : pot_whitelist) {
                            if (pot.equals(pot_id)) {
                                break inner;
                            }
                        }
                        return true;
                    }
                }

                int current_water = plugin.getWateringCanManager().getCurrentWater(item_in_hand);
                if (current_water <= 0) return true;

                PotWaterEvent potWaterEvent = new PotWaterEvent(player, item_in_hand, 1, location);
                Bukkit.getPluginManager().callEvent(potWaterEvent);
                if (potWaterEvent.isCancelled()) {
                    return true;
                }

                current_water--;
                this.waterPot(wateringCanConfig.getWidth(), wateringCanConfig.getLength(), location, player.getLocation().getYaw(), pot_id, wateringCanConfig.getParticle(), potWaterEvent.getWater());
                this.doWateringCanActions(player, item_in_hand, wateringCanConfig, current_water);
                break outer;
            }

            // use fertilizer
            FertilizerConfig fertilizerConfig = plugin.getFertilizerManager().getConfigByItemID(item_in_hand_id);
            if (fertilizerConfig != null) {

                if (fertilizerConfig.getRequirements() != null) {
                    CurrentState currentState = new CurrentState(location, player);
                    for (Requirement requirement : fertilizerConfig.getRequirements()) {
                        if (!requirement.isConditionMet(currentState)) {
                            return true;
                        }
                    }
                }

                FertilizerUseEvent fertilizerUseEvent = new FertilizerUseEvent(player, item_in_hand, fertilizerConfig.getKey(), location);
                Bukkit.getPluginManager().callEvent(fertilizerUseEvent);
                if (fertilizerUseEvent.isCancelled()) {
                    return true;
                }

                if (fertilizerConfig.isBeforePlant() && plugin.getCropManager().containsStage(plugin.getPlatformInterface().getAnyItemIDAt(location.clone().add(0,1,0)))) {
                    AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.beforePlant);
                    return true;
                }

                if (player.getGameMode() != GameMode.CREATIVE) item_in_hand.setAmount(item_in_hand.getAmount() - 1);
                player.swingMainHand();

                if (fertilizerConfig.getSound() != null) {
                    AdventureUtils.playerSound(player, fertilizerConfig.getSound());
                }
                if (fertilizerConfig.getParticle() != null) {
                    location.getWorld().spawnParticle(fertilizerConfig.getParticle(), location.clone().add(0.5,1.1,0.5), 5,0.25,0.1,0.25, 0);
                }
                plugin.getWorldDataManager().addFertilizerToPot(SimpleLocation.getByBukkitLocation(location), new Fertilizer(fertilizerConfig), pot_id);
                break outer;
            }
        }

        Pot potData = Optional.ofNullable(plugin.getWorldDataManager().getPotData(SimpleLocation.getByBukkitLocation(location))).orElse(new Pot(pot_id, null, 0));
        GrowingCrop growingCrop = plugin.getWorldDataManager().getCropData(SimpleLocation.getByBukkitLocation(location));
        PotInfoEvent potInfoEvent = new PotInfoEvent(player, location, item_in_hand, potData.getFertilizer(), potData.getWater(), growingCrop);
        Bukkit.getPluginManager().callEvent(potInfoEvent);

        if (potConfig.getPotInfoItem() != null && !item_in_hand_id.equals(potConfig.getPotInfoItem())) {
            return true;
        }

        WaterAmountHologram waterAmountHologram = potConfig.getWaterAmountHologram();
        if (waterAmountHologram != null) {
            double offset = 0;
            StageConfig stageConfig = plugin.getCropManager().getStageConfig(plugin.getPlatformInterface().getAnyItemIDAt(location.clone().add(0,1,0)));
            if (stageConfig != null) {
                offset = stageConfig.getOffsetCorrection();
            }
            plugin.getHologramManager().showHologram(player,
                    location.clone().add(0.5,waterAmountHologram.getOffset() + offset,0.5),
                    AdventureUtils.getComponentFromMiniMessage(waterAmountHologram.getContent(potData.getWater(), potConfig.getMaxStorage())),
                    waterAmountHologram.getDuration() * 1000,
                    waterAmountHologram.getMode(),
                    waterAmountHologram.getTextDisplayMeta()
            );
        }

        FertilizerHologram fertilizerHologram = potConfig.getFertilizerHologram();
        if (fertilizerHologram != null && potData.getFertilizer() != null) {
            double offset = 0;
            StageConfig stageConfig = plugin.getCropManager().getStageConfig(plugin.getPlatformInterface().getAnyItemIDAt(location.clone().add(0,1,0)));
            if (stageConfig != null) {
                offset = stageConfig.getOffsetCorrection();
            }
            plugin.getHologramManager().showHologram(player,
                    location.clone().add(0.5,fertilizerHologram.getOffset() + offset,0.5),
                    AdventureUtils.getComponentFromMiniMessage(fertilizerHologram.getContent(potData.getFertilizer())),
                    fertilizerHologram.getDuration() * 1000,
                    fertilizerHologram.getMode(),
                    fertilizerHologram.getTextDisplayMeta()
            );
        }
        return true;
    }

    private void doWateringCanActions(Player player, ItemStack item_in_hand, WateringCanConfig wateringCanConfig, int current_water) {
        if (wateringCanConfig.hasActionBar()) {
            AdventureUtils.playerActionbar(player, wateringCanConfig.getActionBarMsg(current_water));
        }
        if (wateringCanConfig.getSound() != null) {
            AdventureUtils.playerSound(player, wateringCanConfig.getSound());
        }
        plugin.getWateringCanManager().setWater(item_in_hand, current_water, wateringCanConfig);
    }

    public boolean onBreakPot(@Nullable Entity entity, String id, Location location, Cancellable event) {
        PotConfig potConfig = plugin.getPotManager().getPotConfigByBlockID(id);
        if (potConfig == null) {
            return false;
        }

        PotBreakEvent potBreakEvent = new PotBreakEvent(entity, location, potConfig.getKey());
        Bukkit.getPluginManager().callEvent(potBreakEvent);
        if (potBreakEvent.isCancelled()) {
            event.setCancelled(true);
            return true;
        }

        Location above_loc = location.clone().add(0,1,0);
        String above_id = plugin.getPlatformInterface().getAnyItemIDAt(above_loc);
        // has item above
        // is a crop
        if (onBreakCrop(entity, above_id, above_loc, event)) {
            // The event might be cancelled if the player doesn't meet the break requirements
            if (event.isCancelled()) {
                return true;
            }
            plugin.getPlatformInterface().removeCustomItemAt(above_loc);
        }

        plugin.getWorldDataManager().removePotData(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    private boolean onBreakSprinkler(Player player, String id, Location location, Cancellable event) {
        SprinklerConfig sprinklerConfig = plugin.getSprinklerManager().getConfigByItemID(id);
        if (sprinklerConfig == null) {
            return false;
        }

        SprinklerBreakEvent sprinklerBreakEvent = new SprinklerBreakEvent(player, location, sprinklerConfig.getKey());
        Bukkit.getPluginManager().callEvent(sprinklerBreakEvent);
        if (sprinklerBreakEvent.isCancelled()) {
            event.setCancelled(true);
            return true;
        }

        plugin.getWorldDataManager().removeSprinklerData(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    private boolean onBreakCrop(@Nullable Entity entity, String id, Location location, Cancellable event) {
        if (plugin.getCropManager().isDeadCrop(id)) {
            return true;
        }

        CropConfig cropConfig = plugin.getCropManager().getCropConfigByStage(id);
        if (cropConfig == null) return false;

        // The entity might be other creatures when the pot is FARMLAND
        // because of vanilla farmland mechanics
        if (entity instanceof Player player) {
            if (!canBreak(player, cropConfig, location)) {
                event.setCancelled(true);
                return true;
            }
            CropBreakEvent cropBreakEvent = new CropBreakEvent(entity, cropConfig.getKey(), id, location);
            Bukkit.getPluginManager().callEvent(cropBreakEvent);
            if (cropBreakEvent.isCancelled()) {
                return true;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                StageConfig stageConfig = plugin.getCropManager().getStageConfig(id);
                if (stageConfig != null) {
                    Action[] breakActions = stageConfig.getBreakActions();
                    if (breakActions != null) {
                        for (Action action : breakActions) {
                            action.doOn(player, SimpleLocation.getByBukkitLocation(location), cropConfig.getCropMode());
                        }
                    }
                }
            }
        } else {
            CropBreakEvent cropBreakEvent = new CropBreakEvent(entity, cropConfig.getKey(), id, location);
            Bukkit.getPluginManager().callEvent(cropBreakEvent);
            if (cropBreakEvent.isCancelled()) {
                return true;
            }
            StageConfig stageConfig = plugin.getCropManager().getStageConfig(id);
            if (stageConfig != null) {
                Action[] breakActions = stageConfig.getBreakActions();
                if (breakActions != null) {
                    for (Action action : breakActions) {
                        action.doOn(null, SimpleLocation.getByBukkitLocation(location), cropConfig.getCropMode());
                    }
                }
            }
        }
        plugin.getWorldDataManager().removeCropData(SimpleLocation.getByBukkitLocation(location));
        return true;
    }

    public boolean canBreak(Player player, CropConfig cropConfig, Location crop_loc) {
        Requirement[] requirements = cropConfig.getBreakRequirements();
        if (requirements == null) return true;
        CurrentState currentState = new CurrentState(crop_loc, player);
        for (Requirement requirement : requirements) {
            if (!requirement.isConditionMet(currentState)) {
                return false;
            }
        }
        return true;
    }

    private void waterPot(int width, int length, Location location, float yaw, String id, @Nullable Particle particle, int water){
        int extend = width / 2;
        if (yaw < 45 && yaw > -135) {
            if (yaw > -45) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(i, 0, -1);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(0,0,1);
                        tryToWaterPot(tempLoc, id, particle, water);
                    }
                }
            } else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(-1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(1,0,0);
                        tryToWaterPot(tempLoc, id, particle, water);
                    }
                }
            }
        } else {
            if (yaw > 45 && yaw < 135) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(1,0,0);
                        tryToWaterPot(tempLoc, id, particle, water);
                    }
                }
            } else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(i, 0, 1);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(0,0,1);
                        tryToWaterPot(tempLoc, id, particle, water);
                    }
                }
            }
        }
    }

    private void tryToWaterPot(Location location, String pot_id, @Nullable Particle particle, int water) {
        String blockID = plugin.getPlatformInterface().getBlockID(location.getBlock());
        String current_id = plugin.getPotManager().getPotKeyByBlockID(blockID);
        if (current_id != null && current_id.equals(pot_id)) {
            plugin.getWorldDataManager().addWaterToPot(SimpleLocation.getByBukkitLocation(location), water, pot_id);
            if (particle != null)
                location.getWorld().spawnParticle(particle, location.clone().add(0.5,1, 0.5),3,0.1,0.1,0.1);
        }
    }

    public boolean onInteractWithWateringCan(Player player, String item_in_hand_id, ItemStack item_in_hand, @Nullable String id, @Nullable Location location) {
        WateringCanConfig wateringCanConfig = plugin.getWateringCanManager().getConfigByItemID(item_in_hand_id);
        if (wateringCanConfig == null) {
            return false;
        }

        if (location != null) {
            if (!ProtectionLib.canPlace(player, location)) {
                return true;
            }
        }

        int current = plugin.getWateringCanManager().getCurrentWater(item_in_hand);
        if (current >= wateringCanConfig.getStorage()) return true;

        int add = 0;

        outer: {
            if (id != null && location != null) {
                for (PositiveFillMethod positiveFillMethod : wateringCanConfig.getPositiveFillMethods()) {
                    if (positiveFillMethod.getId().equals(id)) {
                        add = positiveFillMethod.getAmount();
                        if (positiveFillMethod.getSound() != null) {
                            AdventureUtils.playerSound(player, positiveFillMethod.getSound());
                        }
                        if (positiveFillMethod.getParticle() != null) {
                            location.getWorld().spawnParticle(positiveFillMethod.getParticle(), location.clone().add(0.5,1.1, 0.5),5,0.1,0.1,0.1);
                        }
                        break outer;
                    }
                }
            }

            List<Block> lineOfSight = player.getLineOfSight(null, 5);
            List<String> blockIds = lineOfSight.stream().map(block -> plugin.getPlatformInterface().getBlockID(block)).toList();

            for (PositiveFillMethod positiveFillMethod : wateringCanConfig.getPositiveFillMethods()) {
                int index = 0;
                for (String blockId : blockIds) {
                    if (positiveFillMethod.getId().equals(blockId)) {
                        add = positiveFillMethod.getAmount();
                        if (positiveFillMethod.getSound() != null) {
                            AdventureUtils.playerSound(player, positiveFillMethod.getSound());
                        }
                        if (positiveFillMethod.getParticle() != null) {
                            Block block = lineOfSight.get(index);
                            block.getWorld().spawnParticle(positiveFillMethod.getParticle(), block.getLocation().add(0.5,1.1, 0.5),5,0.1,0.1,0.1);
                        }
                        break;
                    }
                    index++;
                }
            }
        }

        if (add == 0) return true;
        int finalWater = Math.min(wateringCanConfig.getStorage(), add + current);
        plugin.getWateringCanManager().setWater(item_in_hand, finalWater, wateringCanConfig);
        if (wateringCanConfig.hasActionBar()) {
            AdventureUtils.playerActionbar(player, wateringCanConfig.getActionBarMsg(finalWater));
        }
        return true;
    }
}
