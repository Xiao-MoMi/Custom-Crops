package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakCustomBlock implements Listener {

    @EventHandler
    public void breakCustomBlock(CustomBlockBreakEvent event){
        FileConfiguration config = CustomCrops.instance.getConfig();
        Player player =event.getPlayer();
        Location location = event.getBlock().getLocation();
        if(event.getNamespacedID().contains("stage")){
            if(player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(event.getNamespacedID(), location);
                CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location)).getLoot().forEach(itemStack -> {
                    location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack);
                    CustomBlock.remove(location);
                });
            }
        }else if(event.getNamespacedID().equalsIgnoreCase(config.getString("config.watered-pot")) || event.getNamespacedID().equalsIgnoreCase(config.getString("config.pot"))){
            if(CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location.clone().add(0,1,0))) != null){
                if(CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location.clone().add(0,1,0))).getNamespacedID().contains("stage")){
                    CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location.clone().add(0,1,0))).getLoot().forEach(itemStack -> {
                        location.getWorld().dropItem(location.clone().add(0.5,1.2,0.5), itemStack);
                    });
                    CustomBlock.remove(location.clone().add(0,1,0));
                }
            }
        }
    }
}
