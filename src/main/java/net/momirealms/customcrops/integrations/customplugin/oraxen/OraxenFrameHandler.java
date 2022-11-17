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
import io.th0rgal.oraxen.api.events.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.utils.drops.Drop;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.integrations.AntiGrief;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.FurnitureUtil;
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

        String id = event.getMechanic().getItemID();
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (id.equals(BasicItemConfig.dryPot)
                || id.equals(BasicItemConfig.wetPot)) {

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }

            if (!canProceedAction(player, location)) return;

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

        String id = event.getMechanic().getItemID();
        if (id == null) return;

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }
        if (MainConfig.enableCrow && id.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrow(event.getBlock().getLocation());
            return;
        }

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

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        String blockID = event.getMechanic().getItemID();
        if (blockID.equals(BasicItemConfig.dryPot) || blockID.equals(BasicItemConfig.wetPot)) {

            Location seedLoc = block.getLocation().clone().add(0,1,0);
            Location potLoc = block.getLocation();

            if (!AntiGrief.testPlace(player, seedLoc)) return;
            if (!canProceedAction(player, seedLoc)) return;
            if (super.tryMisc(player, itemInHand, potLoc)) return;
            if (event.getBlockFace() != BlockFace.UP) return;

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
        if (!canProceedAction(player, location)) return;

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
                        if (SoundConfig.boneMeal.isEnable()) {
                            AdventureUtil.playerSound(
                                    player,
                                    SoundConfig.boneMeal.getSource(),
                                    SoundConfig.boneMeal.getKey(),
                                    1,1
                            );
                        }
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
        if (super.onInteractRipeCrop(location, crop, player)) return;
        ItemFrame itemFrame = cropManager.getCustomInterface().placeFurniture(location, crop.getReturnStage());
        if (crop.canRotate() && itemFrame != null) {
            itemFrame.setRotation(FurnitureUtil.getRandomRotation());
        }
    }
}