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

import io.th0rgal.oraxen.events.*;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import io.th0rgal.oraxen.utils.drops.Drop;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.integrations.AntiGrief;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OraxenWireHandler extends OraxenHandler{

    public OraxenWireHandler(CropManager cropManager) {
        super(cropManager);
    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;

        // not necessary because string break event would be triggered too

//        String id = event.getNoteBlockMechanic().getItemID();
//        if (id.equals(BasicItemConfig.dryPot) || id.equals(BasicItemConfig.wetPot)) {
//
//            Location location = event.getBlock().getLocation();
//            Player player = event.getPlayer();
//
//            if (!AntiGrief.testBreak(player, location)) {
//                event.setCancelled(true);
//                return;
//            }
//            super.onBreakPot(location);
//
//            Location seedLocation = location.clone().add(0,1,0);
//            StringBlockMechanic mechanic = StringBlockMechanicListener.getStringMechanic(seedLocation.getBlock());
//            if (mechanic == null) return;
//            String seedID = mechanic.getItemID();
//
//            if (seedID.contains("_stage_")) {

//                seedLocation.getBlock().setType(Material.AIR);
//                if (seedID.equals(BasicItemConfig.deadCrop)) return;
                //ripe or not
//                if (hasNextStage(seedID)) {
//                    Drop drop = mechanic.getDrop();
//                    if (drop != null && player.getGameMode() != GameMode.CREATIVE) {
//                        drop.spawns(location, new ItemStack(Material.AIR));
//                    }
//                    super.onBreakUnripeCrop(location);
//                    return;
//                }
//                super.onBreakRipeCrop(seedLocation, seedID, player, false, false);
//            }
//        }
    }

    @Override
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        if (event.isCancelled()) return;

        StringBlockMechanic mechanic = event.getStringBlockMechanic();
        String id = mechanic.getItemID();

        final Player player = event.getPlayer();
        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        if (id.contains("_stage_")) {

            final Block block = event.getBlock();

            if (!AntiGrief.testBreak(player, block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            //Drop seeds
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                Drop drop = mechanic.getDrop();
                if (player.getGameMode() != GameMode.CREATIVE && drop != null)
                    drop.spawns(block.getLocation(), new ItemStack(Material.AIR));
                block.setType(Material.AIR);
            }

            if (id.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(id)) {
                super.onBreakUnripeCrop(block.getLocation());
                return;
            }
            super.onBreakRipeCrop(block.getLocation(), id, player, true, false);
        }
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        //TODO Check if triggered in res

        FurnitureMechanic mechanic = event.getFurnitureMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }
        if (MainConfig.enableCrow && id.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrow(event.getBlock().getLocation());
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Location blockLoc = event.getItemFrame().getLocation();

        if (!AntiGrief.testPlace(player, blockLoc)) return;

        FurnitureMechanic mechanic = event.getFurnitureMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onInteractSprinkler(blockLoc, player, player.getInventory().getItemInMainHand(), sprinkler);
        }
    }

    @Override
    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
        if (event.isCancelled()) return;

        ItemStack itemInHand = event.getItemInHand();
        Location potLoc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (!AntiGrief.testPlace(player, potLoc)) return;
        if (super.tryMisc(event.getPlayer(), itemInHand, potLoc)) return;
        if (event.getBlockFace() != BlockFace.UP) return;

        Location seedLoc = potLoc.clone().add(0,1,0);

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

    @Override
    public void onInteractStringBlock(OraxenStringBlockInteractEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        final Block block = event.getBlock();
        final String id = event.getStringBlockMechanic().getItemID();

        if (id.contains("_stage_")) {

            Location seedLoc = block.getLocation();
            ItemStack itemInHand = event.getItemInHand();
            //ripe crops
            if (!id.equals(BasicItemConfig.deadCrop)) {

                if (!hasNextStage(id)) {
                    if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand != null && itemInHand.getType() != Material.AIR)) {
                        if (!AntiGrief.testBreak(player, seedLoc)) return;

                        block.setType(Material.AIR);
                        this.onInteractRipeCrop(seedLoc, id, player);
                        return;
                    }
                }
                //has next stage
                else if (MainConfig.enableBoneMeal && itemInHand != null && itemInHand.getType() == Material.BONE_MEAL) {
                    if (!AntiGrief.testPlace(player, seedLoc)) return;
                    if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (Math.random() < MainConfig.boneMealChance) {
                        seedLoc.getWorld().spawnParticle(MainConfig.boneMealSuccess, seedLoc.clone().add(0.5,0.5, 0.5),3,0.2,0.2,0.2);
                        if (SoundConfig.boneMeal.isEnable()) {
                            AdventureUtil.playerSound(
                                    player,
                                    SoundConfig.boneMeal.getSource(),
                                    SoundConfig.boneMeal.getKey(),
                                    1,1
                            );
                        }
                        StringBlockMechanicFactory.setBlockModel(block, getNextStage(id));
                    }
                    return;
                }
            }

            if (!AntiGrief.testPlace(player, seedLoc)) return;

            Location potLoc = block.getLocation().clone().subtract(0,1,0);
            super.tryMisc(player, event.getItemInHand(), potLoc);

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
        StringBlockMechanicFactory.setBlockModel(location.getBlock(), crop.getReturnStage());
    }
}