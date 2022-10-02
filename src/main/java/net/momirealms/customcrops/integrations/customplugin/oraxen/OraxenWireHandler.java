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
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicListener;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.integrations.AntiGrief;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.requirements.PlantingCondition;
import net.momirealms.customcrops.objects.requirements.RequirementInterface;
import net.momirealms.customcrops.utils.FurnitureUtil;
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
        String id = event.getNoteBlockMechanic().getItemID();
        if (id.equals(BasicItemConfig.dryPot) || id.equals(BasicItemConfig.wetPot)) {

            Location location = event.getBlock().getLocation();
            Player player = event.getPlayer();

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }
            super.onBreakPot(location);

            Location seedLocation = location.clone().add(0,1,0);
            StringBlockMechanic mechanic = StringBlockMechanicListener.getStringMechanic(seedLocation.getBlock());
            if (mechanic == null) return;
            String seedID = mechanic.getItemID();
            if (seedID.contains("_stage_")) {
                seedLocation.getBlock().setType(Material.AIR);
                if (seedID.equals(BasicItemConfig.deadCrop)) return;
                //ripe or not
                if (hasNextStage(seedID)) {
                    for (ItemStack item : seedLocation.getBlock().getDrops())
                        player.getWorld().dropItemNaturally(seedLocation, item);
                }
                else {
                    super.onBreakRipeCrop(seedLocation, seedID, player, false, false);
                }
            }
        }
    }

    @Override
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        if (event.isCancelled()) return;
        String id = event.getStringBlockMechanic().getItemID();
        if (id.contains("_stage_")) {

            final Player player = event.getPlayer();
            final Block block = event.getBlock();

            if (!AntiGrief.testBreak(player, event.getBlock().getLocation())) {
                event.setCancelled(true);
                return;
            }

            //Drop seeds
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                if (player.getGameMode() != GameMode.CREATIVE)
                    for (ItemStack item : block.getDrops())
                        player.getWorld().dropItemNaturally(block.getLocation(), item);
                block.setType(Material.AIR);
            }

            if (id.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(id)) return;
            super.onBreakRipeCrop(block.getLocation(), id, player, true, false);
        }
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;
        FurnitureMechanic mechanic = event.getFurnitureMechanic();
        if (mechanic == null) return;
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(mechanic.getItemID());
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;
        FurnitureMechanic mechanic = event.getFurnitureMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onInteractSprinkler(event.getBlock().getLocation(), event.getPlayer(), event.getPlayer().getActiveItem(), sprinkler);
        }
    }

    @Override
    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
        if (event.isCancelled()) return;

        ItemStack itemInHand = event.getItemInHand();
        Location potLoc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (!AntiGrief.testPlace(player, potLoc)) return;

        super.tryMisc(event.getPlayer(), itemInHand, potLoc);

        if (event.getBlockFace() != BlockFace.UP) return;
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
        String id = OraxenItems.getIdByItem(itemInHand);

        if (id.endsWith("_seeds")) {
            String cropName = id.substring(0, id.length() - 6);
            Crop crop = CropConfig.CROPS.get(cropName);
            if (crop == null) return;

            Location seedLoc = potLoc.clone().add(0,1,0);
            CustomWorld customWorld = cropManager.getCustomWorld(seedLoc.getWorld());
            if (customWorld == null) return;

            if (FurnitureUtil.hasFurniture(seedLoc)) return;
            if (seedLoc.getBlock().getType() != Material.AIR) return;

            PlantingCondition plantingCondition = new PlantingCondition(seedLoc, player);

            for (RequirementInterface requirement : crop.getRequirements()) {
                if (!requirement.isConditionMet(plantingCondition)) {
                    return;
                }
            }
            StringBlockMechanicFactory.setBlockModel(seedLoc.getBlock(), id.substring(0, id.length() - 5) + "stage_1");
            customWorld.addCrop(seedLoc, cropName);
        }
    }

    @Override
    public void onInteractStringBlock(OraxenStringBlockInteractEvent event) {
        if (event.isCancelled()) return;

        String id = event.getStringBlockMechanic().getItemID();
        Player player = event.getPlayer();

        if (id.contains("_stage_")) {
            if (!id.equals(BasicItemConfig.deadCrop)) {
                //ripe crops
                if (!hasNextStage(id) && MainConfig.canRightClickHarvest) {
                    Block seedBlock = event.getBlock();
                    Location seedLoc = seedBlock.getLocation();
                    seedBlock.setType(Material.AIR);
                    this.onInteractRipeCrop(seedLoc, id, event.getPlayer());
                }

                else {
                    Location potLoc = event.getBlock().getLocation().clone().subtract(0,1,0);
                    super.tryMisc(player, event.getItemInHand(), potLoc);
                }
            }
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
        StringBlockMechanicFactory.setBlockModel(location.getBlock(), crop.getReturnStage());
    }
}