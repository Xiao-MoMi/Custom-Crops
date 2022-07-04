package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomFurniture;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.utils.HoloUtil;
import net.momirealms.customcrops.utils.Sprinkler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


public class InteractEntity implements Listener {

    private final CustomCrops plugin;
    private final HashMap<Player, Long> coolDown = new HashMap<>();

    public InteractEntity(CustomCrops plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event){
        Entity entity = event.getRightClicked();
        if(entity instanceof ArmorStand armorStand){
            if(CustomFurniture.byAlreadySpawned(armorStand) == null) return;
            Sprinkler config = ConfigReader.SPRINKLERS.get(CustomFurniture.byAlreadySpawned(armorStand).getId());
            if(config != null){
                long time = System.currentTimeMillis();
                Player player = event.getPlayer();
                if (time - (coolDown.getOrDefault(player, time - 1000)) < 1000) {
                    return;
                }
                coolDown.put(player, time);
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                Location location = armorStand.getLocation();
                String world = location.getWorld().getName();
                int x = location.getBlockX();
                int z = location.getBlockZ();
                int maxWater = config.getWater();
                int currentWater = 0;
                Location loc = location.subtract(0,1,0).getBlock().getLocation().add(0,1,0);
                Sprinkler sprinkler = SprinklerManager.Cache.get(loc);
                if (itemStack.getType() == Material.WATER_BUCKET){
                    itemStack.setType(Material.BUCKET);
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL,1,1);
                    if (sprinkler != null){
                        currentWater = sprinkler.getWater();
                        currentWater += ConfigReader.Config.sprinklerRefill;
                        if (currentWater > maxWater){
                            currentWater = maxWater;
                        }
                        sprinkler.setWater(currentWater);
                    }else {
                        String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z + ".water";
                        currentWater = plugin.getSprinklerManager().data.getInt(path);
                        currentWater += ConfigReader.Config.sprinklerRefill;
                        if (currentWater > maxWater){
                            currentWater = maxWater;
                        }
                        plugin.getSprinklerManager().data.set(path, currentWater);
                    }
                }else {
                    if (sprinkler != null){
                        currentWater = sprinkler.getWater();
                    }else {
                        String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z + ".water";
                        currentWater = plugin.getSprinklerManager().data.getInt(path);
                    }
                }
                if (ConfigReader.Message.hasSprinklerInfo){
                    String string = ConfigReader.Message.sprinklerLeft + ConfigReader.Message.sprinklerFull.repeat(currentWater) +
                            ConfigReader.Message.sprinklerEmpty.repeat(maxWater - currentWater) + ConfigReader.Message.sprinklerRight;
                    if(HoloUtil.cache.get(player) == null) {
                        HoloUtil.showHolo(string.replace("{max_water}", String.valueOf(maxWater)).replace("{water}", String.valueOf(currentWater)), player, location.add(0, ConfigReader.Message.sprinklerOffset,0), ConfigReader.Message.sprinklerTime);
                    }
                }
            }
        }
    }
}
