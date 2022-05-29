package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BreakCustomBlock implements Listener {

    @EventHandler
    public void breakCustomBlock(CustomBlockBreakEvent event){
        Player player =event.getPlayer();
        Location location = event.getBlock().getLocation();
        String namespacedId = event.getNamespacedID();

        if(namespacedId.contains("stage")){
            if(player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> {
                    location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack);
                    CustomBlock.remove(location);
                });
            }
        }else if(namespacedId.equalsIgnoreCase(ConfigManager.Config.watered_pot) || namespacedId.equalsIgnoreCase(ConfigManager.Config.pot)){
            World world = location.getWorld();
            Block blockUp = location.add(0,1,0).getBlock();
            if(CustomBlock.byAlreadyPlaced(blockUp) != null){
                if(CustomBlock.byAlreadyPlaced(blockUp).getNamespacedID().contains("stage")){
                    for (ItemStack itemStack : CustomBlock.byAlreadyPlaced(blockUp).getLoot()) {
                        world.dropItem(location.clone().add(0.5, 0.2, 0.5), itemStack);
                    }
                    CustomBlock.remove(location);
                }
            }
        }
    }
}
