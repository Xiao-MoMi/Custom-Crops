package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakCustomBlock implements Listener {

    FileConfiguration config = CustomCrops.instance.getConfig();

    @EventHandler
    public void breakCustomBlock(CustomBlockBreakEvent event){
        Player player =event.getPlayer();
        Location location = event.getBlock().getLocation();
        String namespacedId = event.getNamespacedID();
        if(namespacedId.contains("stage")){
            if(player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location)).getLoot().forEach(itemStack -> {
                    location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack);
                    CustomBlock.remove(location);
                });
            }
        }else if(namespacedId.equalsIgnoreCase(config.getString("config.watered-pot")) || namespacedId.equalsIgnoreCase(config.getString("config.pot"))){
            World world = location.getWorld();
            Block blockUp = world.getBlockAt(location.add(0,1,0));
            if(CustomBlock.byAlreadyPlaced(blockUp) != null){
                if(CustomBlock.byAlreadyPlaced(blockUp).getNamespacedID().contains("stage")){
                    CustomBlock.byAlreadyPlaced(blockUp).getLoot().forEach(itemStack -> {
                        world.dropItem(location.clone().add(0.5,0.2,0.5), itemStack);
                    });
                    CustomBlock.remove(location);
                }
            }
        }
    }
}
