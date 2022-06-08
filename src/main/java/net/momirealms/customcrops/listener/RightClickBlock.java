package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.datamanager.ConfigManager;
import net.momirealms.customcrops.limits.MaxSprinklersPerChunk;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.utils.IAFurniture;
import net.momirealms.customcrops.integrations.IntegrationCheck;
import net.momirealms.customcrops.datamanager.MessageManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RightClickBlock implements Listener {

    @EventHandler
    public void rightClickBlock(PlayerInteractEvent event){

        if(!event.hasItem()) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        CustomStack customStack = CustomStack.byItemStack(event.getItem());

        if(customStack != null){
            Player player = event.getPlayer();
            ItemStack itemStack = event.getItem();
            String namespacedId = customStack.getNamespacedID();
            //检测手中物品是否可能为水壶
            if(itemStack.getType() == Material.WOODEN_SWORD){
                List<Block> lineOfSight = player.getLineOfSight(null, 3);
                //检测范围内是否有水
                boolean hasWater = false;
                for (final Block block : lineOfSight) {
                    if (block.getType() == Material.WATER) {
                        hasWater = true;
                    }
                }
                if(hasWater){

                    if(namespacedId.equalsIgnoreCase(ConfigManager.Config.watering_can_1) ||
                        namespacedId.equalsIgnoreCase(ConfigManager.Config.watering_can_2) ||
                        namespacedId.equalsIgnoreCase(ConfigManager.Config.watering_can_3)) {
                        if(customStack.getMaxDurability() == customStack.getDurability()){
                            MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.can_full, player);
                        }else {
                            customStack.setDurability(customStack.getDurability() + 1);
                            player.getWorld().playSound(player.getLocation(),Sound.ITEM_BUCKET_FILL,1,1);
                        }
                    }
                }
                return;
            }
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getBlockFace() == BlockFace.UP) {

                if(namespacedId.equalsIgnoreCase(ConfigManager.Config.sprinkler_1i) || namespacedId.equalsIgnoreCase(ConfigManager.Config.sprinkler_2i)){
                    Location location = event.getClickedBlock().getLocation();
                    //兼容性检测
                    if(IntegrationCheck.PlaceCheck(location,player)){
                        return;
                    }
                    //高度限制
                    if(event.getClickedBlock().getY() > ConfigManager.Config.maxh || event.getClickedBlock().getY() < ConfigManager.Config.minh){
                        MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.bad_place,player);
                        return;
                    }
                    //此位置是否已有洒水器
                    if(IAFurniture.getFromLocation(location.clone().add(0.5, 1.5, 0.5), location.getWorld())){
                        return;
                    }
                    //区块上限
                    if(MaxSprinklersPerChunk.maxSprinklersPerChunk(location)){
                        MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.limit_sprinkler.replace("{Max}", String.valueOf(ConfigManager.Config.max_sprinkler)),player);
                        return;
                    }
                    if(player.getGameMode() != GameMode.CREATIVE){
                        itemStack.setAmount(itemStack.getAmount() -1);
                    }
                    if(namespacedId.equalsIgnoreCase(ConfigManager.Config.sprinkler_1i)){
                        SprinklerManager.putInstance(location.clone().add(0,1,0),"s1");
                    }else {
                        SprinklerManager.putInstance(location.clone().add(0,1,0),"s2");
                    }
                    IAFurniture.placeFurniture(namespacedId.replace("_item",""),location.clone().add(0,1,0));
                }
            }
        }
    }
}
