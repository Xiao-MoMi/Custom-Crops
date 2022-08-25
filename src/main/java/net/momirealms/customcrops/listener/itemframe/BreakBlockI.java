package net.momirealms.customcrops.listener.itemframe;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.QualityCrop;
import net.momirealms.customcrops.integrations.protection.Integration;
import net.momirealms.customcrops.objects.Crop;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.utils.DropUtil;
import net.momirealms.customcrops.utils.FurnitureUtil;
import net.momirealms.customcrops.utils.LocUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.ThreadLocalRandom;

public class BreakBlockI implements Listener {

    @EventHandler
    public void onBreak(CustomBlockBreakEvent event){
        String namespacedId = event.getNamespacedID();
        if(namespacedId.equalsIgnoreCase(ConfigReader.Basic.watered_pot) || namespacedId.equalsIgnoreCase(ConfigReader.Basic.pot)){
            Location location = event.getBlock().getLocation();
            PotManager.Cache.remove(LocUtil.fromLocation(location));
            World world = location.getWorld();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, event.getPlayer())) return;
            CustomFurniture furniture = FurnitureUtil.getFurniture(location.add(0.5,1.1,0.5));
            if(furniture != null){
                String nsID = furniture.getNamespacedID();
                if(nsID.contains("_stage_")){
                    SimpleLocation simpleLocation = LocUtil.fromLocation(location);
                    if (CropManager.Cache.remove(simpleLocation) == null){
                        CropManager.RemoveCache.add(simpleLocation);
                    }
                    CustomFurniture.remove(furniture.getArmorstand(), false);
                    if (nsID.equals(ConfigReader.Basic.dead)) return;
                    if (ConfigReader.Config.quality){
                        String[] cropNameList = StringUtils.split(StringUtils.split(nsID, ":")[1], "_");
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomFurniture.getInstance(StringUtils.chop(nsID) + nextStage) == null) {
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
                                }
                            }
                            else DropUtil.normalDrop(cropInstance, random, itemLoc, world);
                        }
                    }
                }
            }
        }
    }
}
