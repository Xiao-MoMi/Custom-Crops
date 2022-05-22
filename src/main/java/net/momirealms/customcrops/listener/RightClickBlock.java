package net.momirealms.customcrops.listener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.MaxSprinklersPerChunk;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import net.momirealms.customcrops.IAFurniture;
import net.momirealms.customcrops.Integrations.KingdomsXIntegrations;
import net.momirealms.customcrops.Integrations.ResidenceIntegrations;
import net.momirealms.customcrops.Integrations.WorldGuardIntegrations;
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
        FileConfiguration config = CustomCrops.instance.getConfig();
        if(!event.hasItem()){
            return;
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        //水壶加水
        if(itemStack.getType() == Material.WOODEN_SWORD){
            List<Block> lineOfSight = player.getLineOfSight(null, 3);
            boolean hasWater = false;
            for (final Block block : lineOfSight) {
                if (block.getType() == Material.WATER) {
                    hasWater = true;
                }
            }
            if(hasWater){
                addWater(itemStack,player);
                return;
            }
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getBlockFace() != BlockFace.UP) return;
        if(CustomStack.byItemStack(event.getItem()) == null) return;

        Location location = event.getClickedBlock().getLocation();
        //res兼容
        if(config.getBoolean("config.integration.residence")){
            if(ResidenceIntegrations.checkResBuild(location,player)){
                return;
            }
        }
        //wg兼容
        if(config.getBoolean("config.integration.worldguard")){
            if(WorldGuardIntegrations.checkWGBuild(location,player)){
                return;
            }
        }
        //kingdomsX兼容
        if(config.getBoolean("config.integration.kingdomsX")){
            if(KingdomsXIntegrations.checkKDBuild(location,player)){
                return;
            }
        }
        //是否过高过低
        if(event.getClickedBlock().getY() > config.getInt("config.height.max") || event.getClickedBlock().getY() < config.getInt("config.height.min")){
            MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.not-a-good-place"),player);
            return;
        }

        if(CustomStack.byItemStack(event.getItem()).getNamespacedID().equalsIgnoreCase(config.getString("config.sprinkler-1-item"))){
            if(MaxSprinklersPerChunk.maxSprinklersPerChunk(location)){
                MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.reach-limit-sprinkler").replace("{Max}", config.getString("config.max-sprinklers")),player);
                return;
            }
            if(player.getGameMode() != GameMode.CREATIVE){
                itemStack.setAmount(itemStack.getAmount() -1);
            }
            IAFurniture.placeFurniture(config.getString("config.sprinkler-1"),location.clone().add(0,1,0));
            SprinklerManager.putInstance(location.clone().add(0,1,0),"s1");
        }else if(CustomStack.byItemStack(event.getItem()).getNamespacedID().equalsIgnoreCase(config.getString("config.sprinkler-2-item"))){
            if(MaxSprinklersPerChunk.maxSprinklersPerChunk(location)){
                MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.reach-limit-sprinkler").replace("{Max}", config.getString("config.max-sprinklers")),player);
                return;
            }
            if(player.getGameMode() != GameMode.CREATIVE){
                itemStack.setAmount(itemStack.getAmount() -1);
            }
            IAFurniture.placeFurniture(config.getString("config.sprinkler-2"),location.clone().add(0,1,0));
            SprinklerManager.putInstance(location.clone().add(0,1,0),"s2");
        }
    }
    private void addWater(ItemStack itemStack, Player player){
        FileConfiguration config = CustomCrops.instance.getConfig();
        if(CustomStack.byItemStack(itemStack)!= null){
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if(customStack.getNamespacedID().equalsIgnoreCase(config.getString("config.watering-can-1")) ||
                    customStack.getNamespacedID().equalsIgnoreCase(config.getString("config.watering-can-2")) ||
                    customStack.getNamespacedID().equalsIgnoreCase(config.getString("config.watering-can-3")))
            {
                if(customStack.getMaxDurability() == customStack.getDurability()){
                    MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.can-full"),player);
                }else {
                    customStack.setDurability(customStack.getDurability() + 1);
                    player.playSound(player, Sound.ITEM_BUCKET_FILL,1,1);
                }
            }
        }
    }
}
