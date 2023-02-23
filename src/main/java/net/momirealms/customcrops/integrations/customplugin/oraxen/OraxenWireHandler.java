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

package net.momirealms.customcrops.integrations.customplugin.oraxen;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.api.events.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import io.th0rgal.oraxen.utils.drops.Drop;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.integrations.CCAntiGrief;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class OraxenWireHandler extends OraxenHandler{

    public OraxenWireHandler(CropManager cropManager) {
        super(cropManager);
    }

    @Override
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        if (event.isCancelled()) return;

        StringBlockMechanic mechanic = event.getMechanic();
        String id = mechanic.getItemID();

        final Player player = event.getPlayer();
        if (!id.contains("_stage_")) return;

        final Block block = event.getBlock();
        Location location = block.getLocation();

        if (!CCAntiGrief.testBreak(player, location)) {
            event.setCancelled(true);
            return;
        }

        if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
            event.setCancelled(true);
            Drop drop = mechanic.getDrop();
            if (player.getGameMode() != GameMode.CREATIVE && drop != null)
                drop.spawns(location, new ItemStack(Material.AIR));
            block.setType(Material.AIR);
        }

        if (id.equals(BasicItemConfig.deadCrop)) return;
        if (!isRipe(id)) super.onBreakUnripeCrop(location);
        else {
            Crop crop = customInterface.getCropFromID(id);
            if (crop == null) return;
            if (!checkHarvestRequirements(player, location, crop)) {
                event.setCancelled(true);
                return;
            }
            super.onBreakRipeCrop(location, crop, player, true);
        }
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        FurnitureMechanic mechanic = event.getMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }
        if (MainConfig.enableCrow && id.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrowCache(event.getBlock().getLocation());
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Location blockLoc = event.getItemFrame().getLocation();

        if (!CCAntiGrief.testPlace(player, blockLoc)) return;

        FurnitureMechanic mechanic = event.getMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onInteractSprinkler(blockLoc, player, player.getInventory().getItemInMainHand(), sprinkler);
        }
    }

//    @Override
//    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
//        if (event.isCancelled()) return;
//        if (event.getHand() != EquipmentSlot.HAND) return;
//
//        final ItemStack itemInHand = event.getItemInHand();
//        final Location potLoc = event.getBlock().getLocation();
//        final Player player = event.getPlayer();
//
//        if (super.tryMisc(event.getPlayer(), itemInHand, potLoc)) return;
//        if (event.getBlockFace() != BlockFace.UP) return;
//
//        Location seedLoc = potLoc.clone().add(0,1,0);
//
//        String id = OraxenItems.getIdByItem(itemInHand);
//        if (id != null) {
//            if (id.endsWith("_seeds")) {
//                String cropName = id.substring(0, id.length() - 6);
//                plantSeed(seedLoc, cropName, player, itemInHand);
//            }
//        }
//        else if (MainConfig.enableConvert) {
//            String cropName = MainConfig.vanilla2Crops.get(itemInHand.getType());
//            if (cropName == null) return;
//            plantSeed(seedLoc, cropName, player, itemInHand);
//        }
//    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        super.onPlayerInteract(event);
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Location location = block.getLocation();
        final String blockID = customInterface.getBlockID(location);
        if (blockID == null) return;
        if (blockID.contains("_stage_")) {
            ItemStack itemInHand = event.getItem();
            if (!blockID.equals(BasicItemConfig.deadCrop)) {
                if (isRipe(blockID)) {
                    ItemStack mainHand = player.getInventory().getItemInMainHand();
                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && (mainHand.getType() != Material.AIR || offHand.getType() != Material.AIR))) {
                        if (!CCAntiGrief.testBreak(player, location)) return;
                        Crop crop = customInterface.getCropFromID(blockID);
                        if (crop == null) return;
                        if (!checkHarvestRequirements(player, location, crop)) {
                            event.setCancelled(true);
                            return;
                        }
                        block.setType(Material.AIR);
                        super.onInteractRipeCrop(location, crop, player);
                        if (crop.getReturnStage() != null) customInterface.placeWire(location, crop.getReturnStage());
                        return;
                    }
                }
                //has next stage
                else if (MainConfig.enableBoneMeal && itemInHand != null && itemInHand.getType() == Material.BONE_MEAL) {
                    if (!CCAntiGrief.testPlace(player, location)) return;
                    if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (Math.random() < MainConfig.boneMealChance) {
                        location.getWorld().spawnParticle(MainConfig.boneMealSuccess, location.clone().add(0.5,0.5, 0.5),3,0.2,0.2,0.2);
                        if (SoundConfig.boneMeal.isEnable()) {
                            AdventureUtil.playerSound(
                                    player,
                                    SoundConfig.boneMeal.getSource(),
                                    SoundConfig.boneMeal.getKey(),
                                    1,1
                            );
                        }
                        block.setType(Material.AIR);
                        customInterface.placeWire(location, customInterface.getNextStage(blockID));
                    }
                    return;
                }
            }
            super.tryMisc(player, itemInHand, location.clone().subtract(0,1,0));
        }
        //interact pot (must have an item)
        else if (blockID.equals(BasicItemConfig.wetPot) || blockID.equals(BasicItemConfig.dryPot)) {
            ItemStack itemInHand = event.getItem();
            if (super.tryMisc(player, itemInHand, location)) return;
            if (event.getBlockFace() != BlockFace.UP) return;
            Location seedLoc = location.clone().add(0,1,0);
            String id = OraxenItems.getIdByItem(itemInHand);
            if (id != null) {
                if (id.endsWith("_seeds")) {
                    String cropName = id.substring(0, id.length() - 6);
                    plantSeed(seedLoc, cropName, player, itemInHand);
                }
            }
            else if (MainConfig.enableConvert) {
                String cropName = MainConfig.vanilla2Crops.get(itemInHand.getType());
                if (cropName == null) return;
                plantSeed(seedLoc, cropName, player, itemInHand);
            }
        }
    }

//    @Override
//    public void onInteractStringBlock(OraxenStringBlockInteractEvent event) {
//        if (event.isCancelled()) return;
//        if (event.getHand() != EquipmentSlot.HAND) return;
//
//        final Player player = event.getPlayer();
//        final Block block = event.getBlock();
//        final String id = event.getMechanic().getItemID();
//
//        if (!id.contains("_stage_")) return;
//
//        Location seedLoc = block.getLocation();
//        ItemStack itemInHand = event.getItemInHand();
//        if (!id.equals(BasicItemConfig.deadCrop)) {
//            if (isRipe(id)) {
//                if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand != null && itemInHand.getType() != Material.AIR)) {
//                    if (!CCAntiGrief.testBreak(player, seedLoc)) return;
//                    Crop crop = customInterface.getCropFromID(id);
//                    if (crop == null) return;
//                    if (!checkHarvestRequirements(player, seedLoc, crop)) {
//                        event.setCancelled(true);
//                        return;
//                    }
//                    block.setType(Material.AIR);
//                    super.onInteractRipeCrop(seedLoc, crop, player);
//                    if (crop.getReturnStage() != null) StringBlockMechanicFactory.setBlockModel(seedLoc.getBlock(), crop.getReturnStage());
//                    return;
//                }
//            }
//            //has next stage
//            else if (MainConfig.enableBoneMeal && itemInHand != null && itemInHand.getType() == Material.BONE_MEAL) {
//                if (!CCAntiGrief.testPlace(player, seedLoc)) return;
//                if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
//                if (Math.random() < MainConfig.boneMealChance) {
//                    seedLoc.getWorld().spawnParticle(MainConfig.boneMealSuccess, seedLoc.clone().add(0.5,0.5, 0.5),3,0.2,0.2,0.2);
//                    if (SoundConfig.boneMeal.isEnable()) {
//                        AdventureUtil.playerSound(
//                                player,
//                                SoundConfig.boneMeal.getSource(),
//                                SoundConfig.boneMeal.getKey(),
//                                1,1
//                        );
//                    }
//                    StringBlockMechanicFactory.setBlockModel(block, customInterface.getNextStage(id));
//                }
//                return;
//            }
//        }
//        super.tryMisc(player, event.getItemInHand(), block.getLocation().clone().subtract(0,1,0));
//    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;

        final String id = event.getMechanic().getItemID();
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (!id.equals(BasicItemConfig.dryPot) && !id.equals(BasicItemConfig.wetPot)) return;
        if (!CCAntiGrief.testBreak(player, location)) return;

        label_out: {
            Location seedLocation = location.clone().add(0,1,0);
            String blockID = customInterface.getBlockID(seedLocation);
            if (blockID == null) break label_out;
            if (blockID.contains("_stage_")) {
                if (blockID.equals(BasicItemConfig.deadCrop)) {
                    customInterface.removeBlock(seedLocation);
                    break label_out;
                }
                if (!isRipe(blockID)) {
                    StringBlockMechanic mechanic = (StringBlockMechanic) FurnitureFactory.instance.getMechanic(blockID);
                    if (mechanic == null) break label_out;
                    Drop drop = mechanic.getDrop();
                    if (drop != null && player.getGameMode() != GameMode.CREATIVE) {
                        drop.spawns(seedLocation, new ItemStack(Material.AIR));
                    }
                    customInterface.removeBlock(seedLocation);
                    super.onBreakUnripeCrop(seedLocation);
                }
                else {
                    Crop crop = customInterface.getCropFromID(id);
                    if (crop == null) break label_out;
                    if (!checkHarvestRequirements(player, seedLocation, crop)) {
                        event.setCancelled(true);
                        return;
                    }
                    customInterface.removeBlock(seedLocation);
                    super.onBreakRipeCrop(seedLocation, crop, player, false);
                }
            }
        }
        super.onBreakPot(location);
    }
}