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

package net.momirealms.customcrops.listener.tripwire;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.QualityCrop;
import net.momirealms.customcrops.integrations.protection.Integration;
import net.momirealms.customcrops.objects.Crop;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.fertilizer.YieldIncreasing;
import net.momirealms.customcrops.utils.DropUtil;
import net.momirealms.customcrops.utils.LocUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BreakBlockT implements Listener {

    @EventHandler
    public void onBreak(CustomBlockBreakEvent event){
        String namespacedId = event.getNamespacedID();
        if(namespacedId.contains("_stage_")){
            Player player = event.getPlayer();
            Location location = event.getBlock().getLocation();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, player)) return;
            SimpleLocation simpleLocation = LocUtil.fromLocation(location);
            if (CropManager.Cache.remove(simpleLocation) == null){
                CropManager.RemoveCache.add(simpleLocation);
            }
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack));
                CustomBlock.remove(location);
                return;
            }
            if (!ConfigReader.Config.quality || namespacedId.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(StringUtils.split(namespacedId, ":")[1], "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomBlock.getInstance(StringUtils.chop(namespacedId) + nextStage) == null) {
                Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, ()-> {
                    if (location.getBlock().getType() != Material.AIR) return;
                    Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                    ThreadLocalRandom current = ThreadLocalRandom.current();
                    int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                    Location itemLoc = location.clone().add(0.5,0.2,0.5);
                    World world = location.getWorld();
                    List<String> commands = cropInstance.getCommands();
                    Fertilizer fertilizer = PotManager.Cache.get(LocUtil.fromLocation(location.clone().subtract(0,1,0)));
                    if (commands != null)
                        Bukkit.getScheduler().runTask(CustomCrops.plugin, ()-> {
                            for (String command : commands)
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                        });
                    if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0)
                        Bukkit.getScheduler().runTask(CustomCrops.plugin, ()-> ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP()));
                    if (fertilizer != null){
                        if (fertilizer instanceof QualityCrop qualityCrop){
                            int[] weights = qualityCrop.getChance();
                            double weightTotal = weights[0] + weights[1] + weights[2];
                            Bukkit.getScheduler().runTask(CustomCrops.plugin, ()-> {
                                for (int i = 0; i < random; i++){
                                    double ran = Math.random();
                                    if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                    else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                    else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                                }
                            });
                        }
                        else Bukkit.getScheduler().runTask(CustomCrops.plugin, ()-> DropUtil.normalDrop(cropInstance, random, itemLoc, world));
                    }
                    else Bukkit.getScheduler().runTask(CustomCrops.plugin, ()-> DropUtil.normalDrop(cropInstance, random, itemLoc, world));
                });
            }
        }
        else if(namespacedId.equalsIgnoreCase(ConfigReader.Basic.watered_pot) || namespacedId.equalsIgnoreCase(ConfigReader.Basic.pot)){
            Location location = event.getBlock().getLocation();
            PotManager.Cache.remove(LocUtil.fromLocation(location));
            World world = location.getWorld();
            Block blockUp = location.add(0,1,0).getBlock();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, event.getPlayer())) return;
            if(CustomBlock.byAlreadyPlaced(blockUp) != null){
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(blockUp);
                String cropNamespacedId = customBlock.getNamespacedID();
                if(cropNamespacedId.contains("_stage_")){
                    CustomBlock.remove(location);
                    SimpleLocation simpleLocation = LocUtil.fromLocation(location);
                    if (CropManager.Cache.remove(simpleLocation) == null){
                        CropManager.RemoveCache.add(simpleLocation);
                    }
                    if (cropNamespacedId.equals(ConfigReader.Basic.dead)) return;
                    if (ConfigReader.Config.quality){
                        String[] cropNameList = StringUtils.split(StringUtils.split(cropNamespacedId, ":")[1], "_");
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance(StringUtils.chop(cropNamespacedId) + nextStage) == null) {
                            Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                            ThreadLocalRandom current = ThreadLocalRandom.current();
                            int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                            Location itemLoc = location.clone().add(0.5,0.2,0.5);
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtil.fromLocation(location.clone().subtract(0,1,0)));
                            if (fertilizer != null){
                                if (fertilizer instanceof QualityCrop qualityCrop){
                                    int[] weights = qualityCrop.getChance();
                                    double weightTotal = weights[0] + weights[1] + weights[2];
                                    for (int i = 0; i < random; i++){
                                        double ran = Math.random();
                                        if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                        else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                        else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                                    }
                                }else if (fertilizer instanceof YieldIncreasing yieldIncreasing){
                                    if (Math.random() < yieldIncreasing.getChance()){
                                        random += yieldIncreasing.getBonus();
                                    }
                                    DropUtil.normalDrop(cropInstance, random , itemLoc, world);
                                }
                            }
                            else DropUtil.normalDrop(cropInstance, random, itemLoc, world);
                            return;
                        }
                    }
                    for (ItemStack itemStack : customBlock.getLoot())
                        world.dropItem(location.clone().add(0.5, 0.2, 0.5), itemStack);
                }
            }
        }
    }
}
