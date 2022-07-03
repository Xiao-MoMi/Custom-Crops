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
//                if(ConfigReader.SPRINKLERS.get(id) != null){
//                    item.remove();
//                    item.getWorld().dropItem(item.getLocation() ,CustomStack.getInstance(ConfigReader.SPRINKLERS.get(id).getNamespacedID_1()).getItemStack());
//                }else
                if(id.contains("_stage_")){
                    item.remove();
                }
            }
        }
    }
}