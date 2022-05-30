package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.datamanager.ConfigManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class BreakCrops implements Listener {

    @EventHandler
    public void breakCrops(EntitySpawnEvent event){
        Entity entity = event.getEntity();
        if(!(entity instanceof Item)) return;
        if(CustomStack.byItemStack(((Item) entity).getItemStack()) != null){
            String namespacedId = CustomStack.byItemStack(((Item) entity).getItemStack()).getNamespacedID();
            if(namespacedId.equalsIgnoreCase(ConfigManager.Config.sprinkler_1)){
                entity.remove();
                entity.getWorld().dropItem(entity.getLocation() ,CustomStack.getInstance(namespacedId + "_item").getItemStack());
            }else if(namespacedId.equalsIgnoreCase(ConfigManager.Config.sprinkler_2)){
                entity.remove();
                entity.getWorld().dropItem(entity.getLocation() ,CustomStack.getInstance(namespacedId + "_item").getItemStack());
            }else if(namespacedId.contains("_stage_")){
                entity.remove();
            }
        }
    }
}