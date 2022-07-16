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

package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.integrations.Integration;
import net.momirealms.customcrops.utils.CropInstance;
import net.momirealms.customcrops.utils.SimpleLocation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class BreakBlock implements Listener {

    @EventHandler
    public void onBreak(CustomBlockBreakEvent event){
        String namespacedId = event.getNamespacedID();
        if(namespacedId.contains("_stage_")){
            Player player =event.getPlayer();
            Location location = event.getBlock().getLocation();
            for (Integration integration : ConfigReader.Config.integration){
                if(!integration.canBreak(location, player)) return;
            }
            if(player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> {
                    location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack);
                });
                CustomBlock.remove(location);
                return;
            }
            if(!ConfigReader.Config.quality || namespacedId.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(StringUtils.split(namespacedId, ":")[1], "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomBlock.getInstance(StringUtils.chop(namespacedId) + nextStage) == null) {
                CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                ThreadLocalRandom current = ThreadLocalRandom.current();
                int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                Location itemLoc = location.clone().add(0.5,0.2,0.5);
                World world = location.getWorld();
                Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(location.clone().subtract(0,1,0)));
                if (fertilizer != null){
                    if (fertilizer instanceof QualityCrop qualityCrop){
                        int[] weights = qualityCrop.getChance();
                        double weightTotal = weights[0] + weights[1] + weights[2];
                        double rank_1 = weights[0]/(weightTotal);
                        double rank_2 = 1 - weights[1]/(weightTotal);
                        for (int i = 0; i < random; i++){
                            double ran = Math.random();
                            if (ran < rank_1){
                                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                            }else if(ran > rank_2){
                                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                            }else {
                                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                            }
                        }
                    }else {
                        normalDrop(cropInstance, random, itemLoc, world);
                    }
                }
                else {
                    normalDrop(cropInstance, random, itemLoc, world);
                }
            }
        }
        else if(namespacedId.equalsIgnoreCase(ConfigReader.Basic.watered_pot) || namespacedId.equalsIgnoreCase(ConfigReader.Basic.pot)){
            Location location = event.getBlock().getLocation();
            PotManager.Cache.remove(SimpleLocation.fromLocation(location));
            World world = location.getWorld();
            Block blockUp = location.add(0,1,0).getBlock();
            for (Integration integration : ConfigReader.Config.integration){
                if(!integration.canBreak(location, event.getPlayer())) return;
            }
            if(CustomBlock.byAlreadyPlaced(blockUp) != null){
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(blockUp);
                String cropNamespacedId = customBlock.getNamespacedID();
                if(cropNamespacedId.contains("_stage_")){
                    CustomBlock.remove(location);
                    if (cropNamespacedId.equals(ConfigReader.Basic.dead)) return;
                    if (ConfigReader.Config.quality){
                        String[] cropNameList = StringUtils.split(StringUtils.split(cropNamespacedId, ":")[1], "_");
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance(StringUtils.chop(cropNamespacedId) + nextStage) == null) {
                            CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                            ThreadLocalRandom current = ThreadLocalRandom.current();
                            int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                            Location itemLoc = location.clone().add(0.5,0.2,0.5);
                            Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(location.clone().subtract(0,1,0)));
                            if (fertilizer != null){
                                if (fertilizer instanceof QualityCrop qualityCrop){
                                    int[] weights = qualityCrop.getChance();
                                    double weightTotal = weights[0] + weights[1] + weights[2];
                                    double rank_1 = weights[0]/(weightTotal);
                                    double rank_2 = 1 - weights[1]/(weightTotal);
                                    for (int i = 0; i < random; i++){
                                        double ran = Math.random();
                                        if (ran < rank_1){
                                            world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                        }else if(ran > rank_2){
                                            world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                        }else {
                                            world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                                        }
                                    }
                                }
                            }
                            else {
                                normalDrop(cropInstance, random, itemLoc, world);
                            }
                            return;
                        }
                    }
                    for (ItemStack itemStack : customBlock.getLoot()) {
                        world.dropItem(location.clone().add(0.5, 0.2, 0.5), itemStack);
                    }
                    CustomBlock.remove(location);
                }
            }
        }
    }

    static void normalDrop(CropInstance cropInstance, int random, Location itemLoc, World world) {
        for (int i = 0; i < random; i++){
            double ran = Math.random();
            if (ran < ConfigReader.Config.quality_1){
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
            }else if(ran > ConfigReader.Config.quality_2){
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
            }else {
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
            }
        }
    }
}
