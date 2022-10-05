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
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderWireHandler extends ItemsAdderHandler {

    public ItemsAdderWireHandler(CropManager cropManager) {
        super(cropManager);
    }

    //interact sprinkler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(event.getPlayer(), time - 100)) < 100) return;
        coolDown.put(player, time);

        Entity entity = event.getBukkitEntity();

        if (!AntiGrief.testPlace(player, entity.getLocation())) return;

        String namespacedID = event.getNamespacedID();
        if (namespacedID == null) return;
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedID);
        if (sprinkler != null) {
            super.onInteractSprinkler(entity.getLocation(), event.getPlayer(), player.getInventory().getItemInMainHand(), sprinkler);
        }
    }

    //break sprinkler
    public void onBreakFurniture(FurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        String namespacedID = event.getNamespacedID();
        if (namespacedID == null) return;
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedID);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBukkitEntity().getLocation());
        }
    }


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
        Location location = block.getLocation();

        final String blockID = cb.getNamespacedID();

        //interact crop
        if (blockID.contains("_stage_")) {
            ItemStack itemInHand = event.getItem();
            if (!blockID.equals(BasicItemConfig.deadCrop)) {
                if (!hasNextStage(blockID)) {
                    if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand != null && itemInHand.getType() != Material.AIR)) {
                        if (!AntiGrief.testBreak(player, location)) return;
                        CustomBlock.remove(location);
                        this.onInteractRipeCrop(location, blockID, player);
                        return;
                    }
                }
                //has next stage
                else if (MainConfig.enableBoneMeal && itemInHand != null && itemInHand.getType() == Material.BONE_MEAL) {
                    if (!AntiGrief.testPlace(player, location)) return;
                    if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (Math.random() < MainConfig.boneMealChance) {
                        location.getWorld().spawnParticle(MainConfig.boneMealSuccess, location.clone().add(0.5,0.5, 0.5),3,0.2,0.2,0.2);
                        CustomBlock.remove(location);
                        CustomBlock.place(getNextStage(blockID), location);
                    }
                    return;
                }
            }
            if (!AntiGrief.testPlace(player, location)) return;
            Location potLoc = location.clone().subtract(0,1,0);
            super.tryMisc(player, itemInHand, potLoc);
        }

        //interact pot (must have an item)
        else if (blockID.equals(BasicItemConfig.wetPot) || blockID.equals(BasicItemConfig.dryPot)) {
            if (!AntiGrief.testPlace(player, location)) return;

            ItemStack itemInHand = event.getItem();
            if (super.tryMisc(player, itemInHand, location)) return;

            if (event.getBlockFace() != BlockFace.UP) return;

            CustomStack customStack = CustomStack.byItemStack(itemInHand);
            if (customStack == null) return;
            String namespacedID = customStack.getNamespacedID();
            if (namespacedID.endsWith("_seeds")) {
                String cropName = customStack.getId().substring(0, customStack.getId().length() - 6);
                Crop crop = CropConfig.CROPS.get(cropName);
                if (crop == null) return;

                Location seedLoc = location.clone().add(0,1,0);
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

                if (MainConfig.limitation && LimitationUtil.reachWireLimit(location)) {
                    AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.limitWire.replace("{max}", String.valueOf(MainConfig.wireAmount)));
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
                CustomBlock.place(namespacedID.substring(0, namespacedID.length() - 5) + "stage_1", seedLoc);
                customWorld.addCrop(seedLoc, cropName);
            }
        }
    }

    public void onInteractBlock(CustomBlockInteractEvent event) {

        // A broken API Event
//
//        if (event.isCancelled()) return;
//        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
//
//        final Player player = event.getPlayer();
//        final String blockID = event.getNamespacedID();
//        //interact crop
//        if (blockID.contains("_stage_")) {
//
//            if (!blockID.equals(BasicItemConfig.deadCrop)) {
//                //ripe crops
//                if (!hasNextStage(blockID) && MainConfig.canRightClickHarvest) {
//                    Location seedLoc = event.getBlockClicked().getLocation();
//                    CustomBlock.remove(seedLoc);
//                    this.onInteractRipeCrop(seedLoc, blockID, event.getPlayer());
//                }
//
//                else {
//                    Location potLoc = event.getBlockClicked().getLocation().clone().subtract(0,1,0);
//                    super.tryMisc(player, event.getItem(), potLoc);
//                }
//            }
//        }
//
//        //interact pot (must have an item)
//        else if (blockID.equals(BasicItemConfig.wetPot) || blockID.equals(BasicItemConfig.dryPot)) {
//
//            Location seedLoc = event.getBlockClicked().getLocation().clone().add(0,1,0);
//            if (!AntiGrief.testPlace(player, seedLoc)) return;
//
//            ItemStack itemInHand = event.getItem();
//            Location potLoc = event.getBlockClicked().getLocation();
//            super.tryMisc(player, itemInHand, potLoc);
//
//            if (event.getBlockFace() != BlockFace.UP) return;
//            if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
//            CustomStack customStack = CustomStack.byItemStack(itemInHand);
//            if (customStack == null) return;
//            String namespacedID = customStack.getNamespacedID();
//            if (namespacedID.endsWith("_seeds")) {
//                String cropName = customStack.getId().substring(0, customStack.getId().length() - 6);
//                Crop crop = CropConfig.CROPS.get(cropName);
//                if (crop == null) return;
//
//                CustomWorld customWorld = cropManager.getCustomWorld(seedLoc.getWorld());
//                if (customWorld == null) return;
//
//                if (FurnitureUtil.hasFurniture(seedLoc)) return;
//                if (seedLoc.getBlock().getType() != Material.AIR) return;
//
//                PlantingCondition plantingCondition = new PlantingCondition(seedLoc, player);
//
//                if (crop.getRequirements() != null) {
//                    for (RequirementInterface requirement : crop.getRequirements()) {
//                        if (!requirement.isConditionMet(plantingCondition)) {
//                            return;
//                        }
//                    }
//                }
//
//                if (SoundConfig.plantSeed.isEnable()) {
//                    AdventureUtil.playerSound(
//                            player,
//                            SoundConfig.plantSeed.getSource(),
//                            SoundConfig.plantSeed.getKey(),
//                            1,1
//                    );
//                }
//
//                if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
//                CustomBlock.place(namespacedID.substring(0, namespacedID.length() - 5) + "stage_1", seedLoc);
//                customWorld.addCrop(seedLoc, cropName);
//            }
//        }
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
        CustomBlock.place(crop.getReturnStage(), location);
    }

    @Override
    public void onBreakBlock(CustomBlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        String namespacedId = event.getNamespacedID();
        Location location = event.getBlock().getLocation();

        //fix buggy chorus duplication
        super.chorusFix(event.getBlock());

        //break crop
        if (namespacedId.contains("_stage_")) {

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }

            //Drop seeds
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                if (player.getGameMode() != GameMode.CREATIVE)
                    CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack));
                CustomBlock.remove(location);
            }

            if (namespacedId.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(namespacedId)) {
                super.onBreakUnripeCrop(location);
                return;
            }
            super.onBreakRipeCrop(location, namespacedId, player, true, true);

        }

        //break pot
        else if (namespacedId.equals(BasicItemConfig.dryPot)
                || namespacedId.equals(BasicItemConfig.wetPot)) {

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }

            super.onBreakPot(location);

            //Check if there's crop above
            Location seedLocation = location.clone().add(0,1,0);
            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(seedLocation.getBlock());
            if (customBlock == null) return;
            String seedID = customBlock.getNamespacedID();

            if (seedID.contains("_stage_")) {

                CustomBlock.remove(seedLocation);
                if (seedID.equals(BasicItemConfig.deadCrop)) return;
                //ripe or not
                if (hasNextStage(seedID)) {
                    if (player.getGameMode() == GameMode.CREATIVE) return;
                    customBlock.getLoot().forEach(loot -> location.getWorld().dropItemNaturally(seedLocation.getBlock().getLocation(), loot));
                }
                else {
                    super.onBreakRipeCrop(seedLocation, seedID, player, false, true);
                }

            }
        }
    }
}
