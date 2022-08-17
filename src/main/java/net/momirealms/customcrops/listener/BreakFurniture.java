package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakFurniture implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        Sprinkler config = ConfigReader.SPRINKLERS.get(event.getNamespacedID());
        if (config != null){
            SimpleLocation simpleLocation = SimpleLocation.fromLocation(event.getBukkitEntity().getLocation());
            SprinklerManager.Cache.remove(simpleLocation);
            SprinklerManager.RemoveCache.add(simpleLocation);
        }
    }
}