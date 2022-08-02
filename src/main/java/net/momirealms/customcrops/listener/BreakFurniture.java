package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.utils.SimpleLocation;
import net.momirealms.customcrops.utils.Sprinkler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakFurniture implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        Sprinkler config = ConfigReader.SPRINKLERS.get(StringUtils.split(event.getNamespacedID(),":")[1]);
        if (config != null){
            SimpleLocation simpleLocation = SimpleLocation.fromLocation(event.getBukkitEntity().getLocation());
            SprinklerManager.Cache.remove(simpleLocation);
            SprinklerManager.RemoveCache.add(simpleLocation);
        }
    }
}