package net.momirealms.customcrops.listener.tripwire;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.utils.LocUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakFurnitureT implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        Sprinkler config = ConfigReader.SPRINKLERS.get(event.getNamespacedID());
        if (config != null){
            SimpleLocation simpleLocation = LocUtil.fromLocation(event.getBukkitEntity().getLocation());
            if(SprinklerManager.Cache.remove(simpleLocation) == null){
                SprinklerManager.RemoveCache.add(simpleLocation);
            }
        }
    }
}