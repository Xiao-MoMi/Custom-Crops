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

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.th0rgal.oraxen.events.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.events.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.events.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.events.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.utils.drops.Drop;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.SeedPlantEvent;
import net.momirealms.customcrops.config.*;
import net.momirealms.customcrops.integrations.AntiGrief;
import net.momirealms.customcrops.integrations.season.CCSeason;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.requirements.PlantingCondition;
import net.momirealms.customcrops.objects.requirements.RequirementInterface;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.FurnitureUtil;
import net.momirealms.customcrops.utils.LimitationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class OraxenFrameHandler extends OraxenHandler {

    public OraxenFrameHandler(CropManager cropManager) {
        super(cropManager);
    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;

        String id = event.getNoteBlockMechanic().getItemID();
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (id.equals(BasicItemConfig.dryPot)
                || id.equals(BasicItemConfig.wetPot)) {

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }

            super.onBreakPot(location);

            Location seedLocation = location.clone().add(0.5,1.03125,0.5);
            ItemFrame itemFrame = FurnitureUtil.getItemFrame(seedLocation);
            if (itemFrame == null) return;
            String furnitureID = itemFrame.getPersistentDataContainer().get(OraxenHook.FURNITURE, PersistentDataType.STRING);
            if (furnitureID == null) return;
            if (furnitureID.contains("_stage_")) {
                itemFrame.remove();
                if (furnitureID.equals(BasicItemConfig.deadCrop)) return;
                if (hasNextStage(furnitureID)) {
                    FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.instance.getMechanic(furnitureID);
                    if (mechanic == null) return;
                    Drop drop = mechanic.getDrop();
                    if (drop != null && player.getGameMode() != GameMode.CREATIVE) {
                        drop.spawns(location, new ItemStack(Material.AIR));
                    }
                    super.onBreakUnripeCrop(location);
                    return;
                }
                super.onBreakRipeCrop(seedLocation, furnitureID, player, false, false);
            }
        }
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        String id = event.getFurnitureMechanic().getItemID();
        if (id == null) return;
        //TODO check if needs anti grief
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }
        if (MainConfig.enableCrow && id.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrow(event.getBlock().getLocation());
            return;
        }
        //TODO check if event.getBlock()
        if (id.contains("_stage_")) {
            if (id.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(id)) {
                super.onBreakUnripeCrop(event.getBlock().getLocation());
                return;
            }
            super.onBreakRipeCrop(event.getBlock().getLocation(), id, event.getPlayer(), false, false);
        }
    }

    @Override
    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {

        final Player player = event.getPlayer();
        final ItemStack itemInHand = event.getItemInHand();
        final Block block = event.getBlock();

        String blockID = event.getNoteBlockMechanic().getItemID();
        if (blockID.equals(BasicItemConfig.dryPot) || blockID.equals(BasicItemConfig.wetPot)) {

            Location seedLoc = block.getLocation().clone().add(0,1,0);
            Location potLoc = block.getLocation();

            if (!AntiGrief.testPlace(player, seedLoc)) return;
            if (super.tryMisc(player, itemInHand, potLoc)) return;
            if (event.getBlockFace() != BlockFace.UP) return;

            NBTItem nbtItem = new NBTItem(itemInHand);
            NBTCompound bukkitCompound = nbtItem.getCompound("PublicBukkitValues");
            if (bukkitCompound == null) return;
            String id = bukkitCompound.getString("oraxen:id");
            if (id == null || !id.endsWith("_seeds")) return;

            String cropName = id.substring(0, id.length() - 6);
            Crop crop = CropConfig.CROPS.get(cropName);
            if (crop == null) return;

            CustomWorld customWorld = cropManager.getCustomWorld(seedLoc.getWorld());
            if (customWorld == null) return;

            if (FurnitureUtil.hasFurniture(seedLoc.clone().add(0.5,0.03125,0.5))) return;
            if (seedLoc.getBlock().getType() != Material.AIR) return;

            PlantingCondition plantingCondition = new PlantingCondition(seedLoc, player);
            if (crop.getRequirements() != null) {
                for (RequirementInterface requirement : crop.getRequirements()) {
                    if (!requirement.isConditionMet(plantingCondition)) {
                        return;
                    }
                }
            }
            if (MainConfig.limitation && LimitationUtil.reachFrameLimit(potLoc)) {
                AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.limitFrame.replace("{max}", String.valueOf(MainConfig.frameAmount)));
                return;
            }
            CCSeason[] seasons = crop.getSeasons();
            if (SeasonConfig.enable && seasons != null) {
                if (cropManager.isWrongSeason(seedLoc, seasons)) {
                    if (MainConfig.notifyInWrongSeason) AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.wrongSeason);
                    if (MainConfig.preventInWrongSeason) return;
                }
            }

            SeedPlantEvent seedPlantEvent = new SeedPlantEvent(player, seedLoc, crop);
            Bukkit.getPluginManager().callEvent(seedPlantEvent);
            if (seedPlantEvent.isCancelled()) {
                return;
            }

            if (SoundConfig.plantSeed.isEnable()) {
                AdventureUtil.playerSound(
                        player,
                        SoundConfig.plantSeed.getSource(),
                        SoundConfig.plantSeed.getKey(),
                        1,1
                );
            }

            if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
            ItemFrame itemFrame = customInterface.placeFurniture(seedLoc, id.substring(0, id.length() - 5) + "stage_1");
            if (itemFrame != null) {
                itemFrame.setRotation(FurnitureUtil.getRandomRotation());
            }
            customWorld.addCrop(seedLoc, cropName);
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        String id = event.getFurnitureMechanic().getItemID();
        if (id == null) return;

        final Player player = event.getPlayer();
        final ItemFrame itemFrame = event.getItemFrame();
        final Location location = itemFrame.getLocation();

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            if (!AntiGrief.testPlace(player, itemFrame.getLocation())) return;
            super.onInteractSprinkler(location, player, player.getInventory().getItemInMainHand(), sprinkler);
            return;
        }

        if (id.contains("_stage_")) {
            if (!id.equals(BasicItemConfig.deadCrop)) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (!hasNextStage(id)) {
                    if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand.getType() != Material.AIR)) {
                        if (!AntiGrief.testBreak(player, location)) return;
                        itemFrame.remove();
                        this.onInteractRipeCrop(location, id, player);
                        return;
                    }
                }
                //has next stage
                else if (MainConfig.enableBoneMeal && itemInHand.getType() == Material.BONE_MEAL) {
                    if (!AntiGrief.testPlace(player, location)) return;
                    if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (Math.random() < MainConfig.boneMealChance) {
                        itemFrame.getWorld().spawnParticle(MainConfig.boneMealSuccess, location.clone().add(0,0.5, 0),3,0.2,0.2,0.2);
                        String nextStage = getNextStage(id);
                        itemFrame.setItem(customInterface.getItemStack(nextStage));
                        itemFrame.getPersistentDataContainer().set(OraxenHook.FURNITURE, PersistentDataType.STRING, nextStage);
                    }
                    return;
                }
            }

            if (!AntiGrief.testPlace(player, location)) return;
            Location potLoc = location.clone().subtract(0,1,0).getBlock().getLocation();
            super.tryMisc(player, player.getInventory().getItemInMainHand(), potLoc);
        }
    }

    private void onInteractRipeCrop(Location location, String id, Player player) {
        Crop crop = getCropFromID(id);
        if (crop == null) return;
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

        Fertilizer fertilizer = customWorld.getFertilizer(location.clone().subtract(0,1,0));
        cropManager.proceedHarvest(crop, player, location, fertilizer);
        if (crop.getReturnStage() == null) {
            customWorld.removeCrop(location);
            return;
        }
        customWorld.addCrop(location, crop.getKey());
        ItemFrame itemFrame = cropManager.getCustomInterface().placeFurniture(location, crop.getReturnStage());
        if (itemFrame != null) {
            itemFrame.setRotation(FurnitureUtil.getRandomRotation());
        }
    }
}