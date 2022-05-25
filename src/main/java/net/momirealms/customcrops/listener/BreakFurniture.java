package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class BreakFurniture implements Listener {

    FileConfiguration config = CustomCrops.instance.getConfig();

    @EventHandler
    public void breakFurniture(EntitySpawnEvent event){
        Entity entity = event.getEntity();
        if(!(entity instanceof Item)) return;
        if(CustomStack.byItemStack(((Item) entity).getItemStack()) != null){
            String namespacedId = CustomStack.byItemStack(((Item) entity).getItemStack()).getNamespacedID();
            if(namespacedId.equalsIgnoreCase(config.getString("config.sprinkler-1"))){
                entity.remove();
                entity.getWorld().dropItem(entity.getLocation() ,CustomStack.getInstance(namespacedId + "_item").getItemStack());
            }else if(namespacedId.equalsIgnoreCase(config.getString("config.sprinkler-2"))){
                entity.remove();
                entity.getWorld().dropItem(entity.getLocation() ,CustomStack.getInstance(namespacedId + "_item").getItemStack());
            }
        }
    }
}
