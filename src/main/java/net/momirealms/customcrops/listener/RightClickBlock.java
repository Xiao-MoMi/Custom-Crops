package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.MaxSprinklersPerChunk;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import net.momirealms.customcrops.IAFurniture;
import net.momirealms.customcrops.Integrations.IntegrationCheck;
import net.momirealms.customcrops.MessageManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
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
                    FileConfiguration config = CustomCrops.instance.getConfig();
                    if(namespacedId.equalsIgnoreCase(config.getString("config.watering-can-1")) ||
                            namespacedId.equalsIgnoreCase(config.getString("config.watering-can-2")) ||
                            namespacedId.equalsIgnoreCase(config.getString("config.watering-can-3"))) {
                            if(customStack.getMaxDurability() == customStack.getDurability()){
                                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.can-full"),player);
                            }else {
                                customStack.setDurability(customStack.getDurability() + 1);
                                player.getWorld().playSound(player.getLocation(),Sound.ITEM_BUCKET_FILL,1,1);
                        }
                    }
                }
                return;
            }
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getBlockFace() == BlockFace.UP) {
                FileConfiguration config = CustomCrops.instance.getConfig();
                if(namespacedId.equalsIgnoreCase(config.getString("config.sprinkler-1-item")) || namespacedId.equalsIgnoreCase(config.getString("config.sprinkler-2-item"))){
                    Location location = event.getClickedBlock().getLocation();
                    //兼容性检测
                    if(IntegrationCheck.PlaceCheck(location,player)){
                        return;
                    }
                    //高度限制
                    if(event.getClickedBlock().getY() > config.getInt("config.height.max") || event.getClickedBlock().getY() < config.getInt("config.height.min")){
                        MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.not-a-good-place"),player);
                        return;
                    }
                    //此位置是否已有洒水器
                    if(MaxSprinklersPerChunk.alreadyPlaced(location)){
                        return;
                    }
                    //区块上限
                    if(MaxSprinklersPerChunk.maxSprinklersPerChunk(location)){
                        MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.reach-limit-sprinkler").replace("{Max}", config.getString("config.max-sprinklers")),player);
                        return;
                    }
                    if(player.getGameMode() != GameMode.CREATIVE){
                        itemStack.setAmount(itemStack.getAmount() -1);
                    }
                    if(namespacedId.equalsIgnoreCase(config.getString("config.sprinkler-1-item"))){
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
