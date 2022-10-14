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

package net.momirealms.customcrops.integrations.customplugin.itemsadder;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderFrameHandler extends ItemsAdderHandler {

    public ItemsAdderFrameHandler(CropManager cropManager) {
        super(cropManager);
    }

    //maybe crop or sprinkler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final String namespacedID = event.getNamespacedID();
        if (namespacedID == null) return;

        final Player player = event.getPlayer();
        final Entity entity = event.getBukkitEntity();
        final Location location = entity.getLocation();;

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedID);
        if (sprinkler != null) {
            if (!AntiGrief.testPlace(player, entity.getLocation())) return;
            super.onInteractSprinkler(entity.getLocation(), player, player.getInventory().getItemInMainHand(), sprinkler);
            return;
        }

        if (namespacedID.contains("_stage_")) {
            if (!namespacedID.equals(BasicItemConfig.deadCrop)) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (!hasNextStage(namespacedID)) {
                    if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand.getType() != Material.AIR)) {
                        if (!AntiGrief.testBreak(player, entity.getLocation())) return;
                        CustomFurniture.remove(entity, false);
                        this.onInteractRipeCrop(location, namespacedID, player);
                        return;
                    }
                }
                //has next stage
                else if (MainConfig.enableBoneMeal && itemInHand.getType() == Material.BONE_MEAL) {
                    if (!AntiGrief.testPlace(player, location)) return;
                    if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (Math.random() < MainConfig.boneMealChance) {
                        entity.getWorld().spawnParticle(MainConfig.boneMealSuccess, location.clone().add(0,0.5, 0),3,0.2,0.2,0.2);
                        if (SoundConfig.boneMeal.isEnable()) {
                            AdventureUtil.playerSound(
                                    player,
                                    SoundConfig.boneMeal.getSource(),
                                    SoundConfig.boneMeal.getKey(),
                                    1,1
                            );
                        }
                        CustomFurniture.remove(entity, false);
                        CustomFurniture.spawn(getNextStage(namespacedID), location.getBlock());
                    }
                    return;
                }
            }

            if (!AntiGrief.testPlace(player, location)) return;
            Location potLoc = location.clone().subtract(0, 1, 0).getBlock().getLocation();
            super.tryMisc(player, player.getInventory().getItemInMainHand(), potLoc);
        }
    }

    public void onBreakFurniture(FurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        final String namespacedId = event.getNamespacedID();
        if (namespacedId == null) return;

        final Location location = event.getBukkitEntity().getLocation();
        final Player player = event.getPlayer();
        //No need for antiGrief checks
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedId);
        if (sprinkler != null) {
            super.onBreakSprinkler(location);
            return;
        }

        if (MainConfig.enableCrow && namespacedId.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrow(event.getBukkitEntity().getLocation());
            return;
        }

        if (namespacedId.contains("_stage_")) {
            if (namespacedId.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(namespacedId)) {
                super.onBreakUnripeCrop(location);
                return;
            }
            super.onBreakRipeCrop(location, namespacedId, player, false, true);
        }
    }

//    Broken
//    //This can only be pot
//    public void onInteractBlock(CustomBlockInteractEvent event) {
//        if (event.isCancelled()) return;
//        String blockID = event.getNamespacedID();
//        if (blockID.equals(BasicItemConfig.dryPot) || blockID.equals(BasicItemConfig.wetPot)) {
//            Location potLoc = event.getBlockClicked().getLocation();
//            super.tryMisc(event.getPlayer(), event.getItem(), potLoc);
//        }
//    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();
        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        super.onPlayerInteract(event);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        CustomBlock cb = CustomBlock.byAlreadyPlaced(block);
        if (cb == null) return;

        final String blockID = cb.getNamespacedID();

        if (!AntiGrief.testPlace(player, block.getLocation())) return;

        //interact crop
        if (blockID.equals(BasicItemConfig.wetPot) || blockID.equals(BasicItemConfig.dryPot)) {

            Location seedLoc = block.getLocation().clone().add(0,1,0);

            if (!AntiGrief.testPlace(player, seedLoc)) return;

            ItemStack itemInHand = event.getItem();
            Location potLoc = block.getLocation();
            if (super.tryMisc(player, itemInHand, potLoc)) return;

            if (event.getBlockFace() != BlockFace.UP) return;

            CustomStack customStack = CustomStack.byItemStack(itemInHand);
            if (customStack == null) return;
            String namespacedID = customStack.getNamespacedID();
            if (namespacedID.endsWith("_seeds")) {
                String cropName = customStack.getId().substring(0, customStack.getId().length() - 6);
                Crop crop = CropConfig.CROPS.get(cropName);
                if (crop == null) return;

                CustomWorld customWorld = cropManager.getCustomWorld(seedLoc.getWorld());
                if (customWorld == null) return;

                if (FurnitureUtil.hasFurniture(seedLoc.clone().add(0.5,0.5,0.5))) return;
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
                CustomFurniture customFurniture = CustomFurniture.spawn(namespacedID.substring(0, namespacedID.length() - 5) + "stage_1", seedLoc.getBlock());
                if (customFurniture != null) {
                    if (customFurniture.getArmorstand() instanceof ItemFrame itemFrame) {
                        itemFrame.setRotation(FurnitureUtil.getRandomRotation());
                    }
                }
                customWorld.addCrop(seedLoc, cropName);
            }
        }
    }

    @Override
    public void onBreakBlock(CustomBlockBreakEvent event) {
        if (event.isCancelled()) return;

        String namespacedId = event.getNamespacedID();
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (!AntiGrief.testBreak(player, location)) {
            event.setCancelled(true);
            return;
        }

        //fix buggy chorus duplication
        chorusFix(event.getBlock());

        if (namespacedId.equals(BasicItemConfig.dryPot)
                || namespacedId.equals(BasicItemConfig.wetPot)) {

            super.onBreakPot(location);

            //Check if there's crop above
            Location seedLocation = location.clone().add(0.5,1.5,0.5);

            ItemFrame itemFrame = FurnitureUtil.getItemFrame(seedLocation);
            if (itemFrame == null) return;
            CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(itemFrame);
            if (customFurniture == null) return;
            String seedID = customFurniture.getNamespacedID();
            if (seedID.contains("_stage_")) {
                CustomFurniture.remove(itemFrame, false);
                if (seedID.equals(BasicItemConfig.deadCrop)) return;
                if (hasNextStage(namespacedId)) {
                    super.onBreakUnripeCrop(location);
                    return;
                }
                super.onBreakRipeCrop(location, namespacedId, player, true, true);
            }
        }
    }

    private void onInteractRipeCrop(Location location, String id, Player player) {

        Crop crop = getCropFromID(id);
        if (crop == null) return;
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

        Fertilizer fertilizer = customWorld.getFertilizer(location.clone().subtract(0,1,0));
        cropManager.proceedHarvest(crop, player, location, fertilizer, true);

        if (crop.getReturnStage() == null) {
            customWorld.removeCrop(location);
            return;
        }
        customWorld.addCrop(location, crop.getKey());
        CustomFurniture customFurniture = CustomFurniture.spawn(crop.getReturnStage(), location.getBlock());
        if (customFurniture != null) {
            if (customFurniture instanceof ItemFrame itemFrame) {
                itemFrame.setRotation(FurnitureUtil.getRandomRotation());
            }
        }
    }
}
