package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.momirealms.customcrops.datamanager.ConfigManager;
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

        String namespacedId = event.getNamespacedID();

        //用于防止玩家使用精准采集获取生长阶段物品
        if(namespacedId.contains("_stage_")){
            Player player =event.getPlayer();
            if(player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                Location location = event.getBlock().getLocation();
                CustomBlock.place(namespacedId, location);
                CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> {
                    location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack);
                    CustomBlock.remove(location);
                });
            }
        }
        //玩家破坏种植盆也会使得农作物掉落
        else if(namespacedId.equalsIgnoreCase(ConfigManager.Config.watered_pot) || namespacedId.equalsIgnoreCase(ConfigManager.Config.pot)){
            Location location = event.getBlock().getLocation();
            World world = location.getWorld();
            Block blockUp = location.add(0,1,0).getBlock();
            if(CustomBlock.byAlreadyPlaced(blockUp) != null){
                if(CustomBlock.byAlreadyPlaced(blockUp).getNamespacedID().contains("_stage_")){
                    for (ItemStack itemStack : CustomBlock.byAlreadyPlaced(blockUp).getLoot()) {
                        world.dropItem(location.clone().add(0.5, 0.2, 0.5), itemStack);
                    }
                    CustomBlock.remove(location);
                }
            }
        }
    }
}
