package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ItemSpawn implements Listener {

    @EventHandler
    public void entitySpawn(EntitySpawnEvent event){
        if(event.getEntity() instanceof Item item) {
            if(CustomStack.byItemStack(item.getItemStack()) != null){
                String id = CustomStack.byItemStack(item.getItemStack()).getId();
                if(id.contains("_stage_")){
                    item.remove();
                }
            }
        }
    }
}