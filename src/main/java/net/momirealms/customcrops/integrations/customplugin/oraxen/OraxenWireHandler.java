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
import io.th0rgal.oraxen.api.events.*;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
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
import net.momirealms.customcrops.objects.Sprinkler;
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
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        if (event.isCancelled()) return;

        StringBlockMechanic mechanic = event.getMechanic();
        String id = mechanic.getItemID();

        final Player player = event.getPlayer();
        if (!id.contains("_stage_")) return;

        final Block block = event.getBlock();
        Location location = block.getLocation();

        if (!AntiGrief.testBreak(player, location)) {
            event.setCancelled(true);
            return;
        }

        if (!canProceedAction(player, location)) return;

        if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
            event.setCancelled(true);
            Drop drop = mechanic.getDrop();
            if (player.getGameMode() != GameMode.CREATIVE && drop != null)
                drop.spawns(location, new ItemStack(Material.AIR));
            block.setType(Material.AIR);
        }

        if (id.equals(BasicItemConfig.deadCrop)) return;
        if (!isRipe(id)) super.onBreakUnripeCrop(location);
        else super.onBreakRipeCrop(location, id, player, true);
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
            super.removeScarecrow(event.getBlock().getLocation());
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Location blockLoc = event.getItemFrame().getLocation();

        if (!AntiGrief.testPlace(player, blockLoc)) return;
        if (!canProceedAction(player, blockLoc)) return;

        FurnitureMechanic mechanic = event.getMechanic();
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

        final ItemStack itemInHand = event.getItemInHand();
        final Location potLoc = event.getBlock().getLocation();
        final Player player = event.getPlayer();

        if (isInCoolDown(player, 50)) return;
        if (!canProceedAction(player, potLoc)) return;
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

        if (isInCoolDown(player, 50)) return;

        final Block block = event.getBlock();
        final String id = event.getMechanic().getItemID();

        if (!id.contains("_stage_")) return;

        Location seedLoc = block.getLocation();
        if (!canProceedAction(player, seedLoc)) return;
        ItemStack itemInHand = event.getItemInHand();
        if (!id.equals(BasicItemConfig.deadCrop)) {
            if (isRipe(id)) {
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
                    StringBlockMechanicFactory.setBlockModel(block, customInterface.getNextStage(id));
                }
                return;
            }
        }
        super.tryMisc(player, event.getItemInHand(), block.getLocation().clone().subtract(0,1,0));
    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;

        final String id = event.getMechanic().getItemID();
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (!id.equals(BasicItemConfig.dryPot) && !id.equals(BasicItemConfig.wetPot)) return;
        if (!canProceedAction(player, location)) return;

        if (!AntiGrief.testBreak(player, location)) {
            event.setCancelled(true);
            return;
        }

        super.onBreakPot(location);
        Location seedLocation = location.clone().add(0,1,0);
        String blockID = customInterface.getBlockID(seedLocation);
        if (blockID == null) return;
        if (blockID.contains("_stage_")) {
            customInterface.removeBlock(seedLocation);
            if (blockID.equals(BasicItemConfig.deadCrop)) return;
            if (!isRipe(blockID)) {
                StringBlockMechanic mechanic = (StringBlockMechanic) FurnitureFactory.instance.getMechanic(blockID);
                if (mechanic == null) return;
                Drop drop = mechanic.getDrop();
                if (drop != null && player.getGameMode() != GameMode.CREATIVE) {
                    drop.spawns(seedLocation, new ItemStack(Material.AIR));
                }
                super.onBreakUnripeCrop(seedLocation);
            }
            else {
                super.onBreakRipeCrop(seedLocation, blockID, player, false);
            }
        }
    }

    private void onInteractRipeCrop(Location location, String id, Player player) {
        Crop crop = customInterface.getCropFromID(id);
        if (crop == null) return;
        if (super.onInteractRipeCrop(location, crop, player)) return;
        if (crop.getReturnStage() != null) StringBlockMechanicFactory.setBlockModel(location.getBlock(), crop.getReturnStage());
    }
}