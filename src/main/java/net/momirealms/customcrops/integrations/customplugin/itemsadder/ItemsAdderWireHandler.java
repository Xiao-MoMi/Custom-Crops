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
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderWireHandler extends ItemsAdderHandler {

    public ItemsAdderWireHandler(CropManager cropManager) {
        super(cropManager);
    }

    //interact sprinkler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        Entity entity = event.getBukkitEntity();
        if (!CCAntiGrief.testPlace(player, entity.getLocation())) return;
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
            return;
        }
        if (MainConfig.enableCrow && namespacedID.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrowCache(event.getBukkitEntity().getLocation());
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        super.onPlayerInteract(event);
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        CustomBlock cb = CustomBlock.byAlreadyPlaced(block);
        if (cb == null) return;
        Location location = block.getLocation();
        final String blockID = cb.getNamespacedID();
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
                        CustomBlock.remove(location);
                        super.onInteractRipeCrop(location, crop, player);
                        if (crop.getReturnStage() != null) CustomBlock.place(crop.getReturnStage(), location);
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
                        CustomBlock.remove(location);
                        CustomBlock.place(customInterface.getNextStage(blockID), location);
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
            CustomStack customStack = CustomStack.byItemStack(itemInHand);
            if (customStack != null) {
                String id = customStack.getId();
                if (id.endsWith("_seeds")) {
                    String cropName = customStack.getId().substring(0, id.length() - 6);
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
    public void onBreakBlock(CustomBlockBreakEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final String namespacedId = event.getNamespacedID();
        final Location location = event.getBlock().getLocation();

        if (!CCAntiGrief.testBreak(player, location)) return;

        if (namespacedId.contains("_stage_")) {
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                if (player.getGameMode() != GameMode.CREATIVE) CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> location.getWorld().dropItemNaturally(location, itemStack));
                CustomBlock.remove(location);
            }
            if (namespacedId.equals(BasicItemConfig.deadCrop)) return;
            if (!isRipe(namespacedId)) super.onBreakUnripeCrop(location);
            else {
                Crop crop = customInterface.getCropFromID(namespacedId);
                if (crop == null) return;
                if (!checkHarvestRequirements(player, location, crop)) {
                    event.setCancelled(true);
                    return;
                }
                super.onBreakRipeCrop(location, crop, player, true);
            }
        }
        else if (namespacedId.equals(BasicItemConfig.dryPot) || namespacedId.equals(BasicItemConfig.wetPot)) {
            label_out: {
                Location seedLocation = location.clone().add(0,1,0);
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(seedLocation.getBlock());
                if (customBlock == null) break label_out;
                String seedID = customBlock.getNamespacedID();
                if (seedID.contains("_stage_")) {
                    if (seedID.equals(BasicItemConfig.deadCrop)) {
                        CustomBlock.remove(seedLocation);
                        break label_out;
                    }
                    if (!isRipe(seedID)) {
                        if (player.getGameMode() == GameMode.CREATIVE) break label_out;
                        CustomBlock.remove(seedLocation);
                        customBlock.getLoot().forEach(loot -> location.getWorld().dropItemNaturally(seedLocation.getBlock().getLocation(), loot));
                    }
                    else {
                        Crop crop = customInterface.getCropFromID(seedID);
                        if (crop == null) break label_out;
                        if (!checkHarvestRequirements(player, seedLocation, crop)) {
                            event.setCancelled(true);
                            return;
                        }
                        CustomBlock.remove(seedLocation);
                        super.onBreakRipeCrop(seedLocation, crop, player, false);
                    }
                }
            }
            super.onBreakPot(location);
        }
    }
}
