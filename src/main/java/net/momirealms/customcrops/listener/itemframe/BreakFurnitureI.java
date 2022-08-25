package net.momirealms.customcrops.listener.itemframe;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.QualityCrop;
import net.momirealms.customcrops.integrations.protection.Integration;
import net.momirealms.customcrops.objects.Crop;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.fertilizer.YieldIncreasing;
import net.momirealms.customcrops.utils.DropUtil;
import net.momirealms.customcrops.utils.LocUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BreakFurnitureI implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        String namespacedID = event.getNamespacedID();
        Sprinkler config = ConfigReader.SPRINKLERS.get(namespacedID);
        if (config != null){
            SimpleLocation simpleLocation = LocUtil.fromLocation(event.getBukkitEntity().getLocation());
            if (SprinklerManager.Cache.remove(simpleLocation) == null){
                SprinklerManager.RemoveCache.add(simpleLocation);
            }
            return;
        }
        if (namespacedID.contains("_stage_")){
            Player player = event.getPlayer();
            Location location = event.getBukkitEntity().getLocation();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, player)) return;
            SimpleLocation simpleLocation = LocUtil.fromLocation(location);
            if (CropManager.Cache.remove(simpleLocation) == null){
                CropManager.RemoveCache.add(simpleLocation);
            }
            if (!ConfigReader.Config.quality || namespacedID.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(StringUtils.split(namespacedID, ":")[1], "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomFurniture.getInstance(StringUtils.chop(namespacedID) + nextStage) == null) {
                Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                ThreadLocalRandom current = ThreadLocalRandom.current();
                int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                Location itemLoc = location.clone().add(0,0.2,0);
                World world = location.getWorld();
                List<String> commands = cropInstance.getCommands();
                Fertilizer fertilizer = PotManager.Cache.get(LocUtil.fromLocation(location.clone().subtract(0,1,0)));
                if (commands != null)
                    for (String command : commands)
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0) ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP());
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
                    else DropUtil.normalDrop(cropInstance, random, itemLoc, world);
                }
                else DropUtil.normalDrop(cropInstance, random, itemLoc, world);
            }
        }
    }
}
