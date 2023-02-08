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

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.api.events.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
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
import net.momirealms.customcrops.utils.FurnitureUtil;
import net.momirealms.customcrops.utils.MiscUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class OraxenFrameHandler extends OraxenHandler {

    public OraxenFrameHandler(CropManager cropManager) {
        super(cropManager);
    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;

        final String id = event.getMechanic().getItemID();
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (!id.equals(BasicItemConfig.dryPot) && !id.equals(BasicItemConfig.wetPot)) return;
        if (!CCAntiGrief.testBreak(player, location)) {
            event.setCancelled(true);
            return;
        }

        label_out: {
            Location seedLocation = location.clone().add(0,1,0);
            ItemFrame itemFrame = FurnitureUtil.getItemFrame(customInterface.getFrameCropLocation(seedLocation));
            if (itemFrame == null) break label_out;
            String furnitureID = itemFrame.getPersistentDataContainer().get(OraxenHook.FURNITURE, PersistentDataType.STRING);
            if (furnitureID == null) break label_out;
            if (furnitureID.contains("_stage_")) {
                if (furnitureID.equals(BasicItemConfig.deadCrop)) {
                    itemFrame.remove();
                    break label_out;
                }
                if (!isRipe(furnitureID)) {
                    itemFrame.remove();
                    FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.instance.getMechanic(furnitureID);
                    if (mechanic == null) break label_out;
                    Drop drop = mechanic.getDrop();
                    if (drop != null && player.getGameMode() != GameMode.CREATIVE) {
                        drop.spawns(seedLocation, new ItemStack(Material.AIR));
                    }
                    super.onBreakUnripeCrop(seedLocation);
                }
                else {
                    Crop crop = customInterface.getCropFromID(furnitureID);
                    if (crop == null) break label_out;
                    if (!checkHarvestRequirements(player, seedLocation, crop)) {
                        event.setCancelled(true);
                        return;
                    }
                    itemFrame.remove();
                    super.onBreakRipeCrop(seedLocation, crop, player, false);
                }
            }
        }
        super.onBreakPot(location);
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        String id = event.getMechanic().getItemID();
        if (id == null) return;

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }

        if (MainConfig.enableCrow && id.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrowCache(event.getBlock().getLocation());
            return;
        }

        if (id.contains("_stage_")) {
            if (id.equals(BasicItemConfig.deadCrop)) return;
            Location seedLoc = event.getBlock().getLocation();
            if (!isRipe(id)) super.onBreakUnripeCrop(seedLoc);
            else {
                Crop crop = customInterface.getCropFromID(id);
                if (crop == null) return;
                if (!checkHarvestRequirements(event.getPlayer(), seedLoc, crop)) {
                    event.setCancelled(true);
                    return;
                }
                super.onBreakRipeCrop(seedLoc, crop, event.getPlayer(), false);
            }
        }
    }

//    @Override
//    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
//        final Player player = event.getPlayer();
//        final ItemStack itemInHand = event.getItemInHand();
//        final Block block = event.getBlock();
//
//        String blockID = event.getMechanic().getItemID();
//        if (!blockID.equals(BasicItemConfig.dryPot) && !blockID.equals(BasicItemConfig.wetPot)) return;
//
//        Location potLoc = block.getLocation();
//        Location seedLoc = potLoc.clone().add(0,1,0);
//
//        if (super.tryMisc(player, itemInHand, potLoc)) return;
//        if (event.getBlockFace() != BlockFace.UP) return;
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
        if (event.getBlockFace() != BlockFace.UP) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        NoteBlockMechanic noteBlockMechanic = OraxenBlocks.getNoteBlockMechanic(block);
        if (noteBlockMechanic != null) {
            final String blockID = noteBlockMechanic.getItemID();
            if (!blockID.equals(BasicItemConfig.wetPot) && !blockID.equals(BasicItemConfig.dryPot)) return;
            Location seedLoc = block.getLocation().clone().add(0,1,0);
            ItemStack itemInHand = event.getItem();
            Location potLoc = block.getLocation();
            if (super.tryMisc(player, itemInHand, potLoc)) return;
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

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        String id = event.getMechanic().getItemID();
        if (id == null) return;

        final Player player = event.getPlayer();
        final ItemFrame itemFrame = event.getItemFrame();
        final Location location = itemFrame.getLocation();

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            if (!CCAntiGrief.testPlace(player, itemFrame.getLocation())) return;
            super.onInteractSprinkler(location, player, player.getInventory().getItemInMainHand(), sprinkler);
            return;
        }

        if (!id.contains("_stage_")) return;
        if (!id.equals(BasicItemConfig.deadCrop)) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (isRipe(id)) {
                if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand.getType() != Material.AIR)) {
                    if (!CCAntiGrief.testBreak(player, location)) return;
                    Crop crop = customInterface.getCropFromID(id);
                    if (crop == null) return;
                    if (!checkHarvestRequirements(player, location, crop)) {
                        event.setCancelled(true);
                        return;
                    }
                    itemFrame.remove();
                    super.onInteractRipeCrop(location, crop, player);
                    if (crop.getReturnStage() != null) {
                        ItemFrame placedFurniture = cropManager.getCustomInterface().placeFurniture(location, crop.getReturnStage());
                        if (crop.canRotate() && placedFurniture != null) itemFrame.setRotation(FurnitureUtil.getRandomRotation());
                    }
                    return;
                }
            }
            else if (MainConfig.enableBoneMeal && itemInHand.getType() == Material.BONE_MEAL) {
                if (!CCAntiGrief.testPlace(player, location)) return;
                if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                if (Math.random() < MainConfig.boneMealChance) {
                    itemFrame.getWorld().spawnParticle(MainConfig.boneMealSuccess, location.clone().add(0,0.5, 0),3,0.2,0.2,0.2);
                    if (SoundConfig.boneMeal.isEnable()) {
                        AdventureUtil.playerSound(
                                player,
                                SoundConfig.boneMeal.getSource(),
                                SoundConfig.boneMeal.getKey(),
                                1,1
                        );
                    }
                    String nextStage = customInterface.getNextStage(id);
                    itemFrame.setItem(customInterface.getItemStack(nextStage));
                    itemFrame.getPersistentDataContainer().set(OraxenHook.FURNITURE, PersistentDataType.STRING, nextStage);
                }
                return;
            }
        }
        super.tryMisc(player, player.getInventory().getItemInMainHand(), MiscUtils.getItemFrameBlockLocation(location.clone().subtract(0,1,0)));
    }
}