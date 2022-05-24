package net.momirealms.customcrops.Crops;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class SprinklerWork {
    public static void sprinklerWork(){
        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        config.getStringList("config.whitelist-worlds").forEach(worldName -> SprinklerManager.getSprinklers(Objects.requireNonNull(Bukkit.getWorld(worldName))).forEach(location -> {
            World world = Bukkit.getWorld(worldName);
            String type = Objects.requireNonNull(data.getString(worldName + "." + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ()));
            if(type.equals("s1")){
                for(int i = -1; i <= 1;i++){
                    for (int j = -1; j <= 1; j++){
                        Location tempLoc = location.clone().add(i,-1,j);
                        waterPot(tempLoc, world, config);
                    }
                }
            }else if(type.equals("s2")){
                for(int i = -2; i <= 2;i++){
                    for (int j = -2; j <= 2; j++){
                        Location tempLoc = location.clone().add(i,-1,j);
                        waterPot(tempLoc, world, config);
                    }
                }
            }
        }));
    }
    private static void waterPot(Location tempLoc, World world, FileConfiguration config) {
        if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLoc)) != null){
            if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLoc)).getNamespacedID().equalsIgnoreCase(config.getString("config.pot"))){
                PacketContainer fakeWater = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
                Bukkit.getScheduler().callSyncMethod(CustomCrops.instance,()->{
                    CustomBlock.remove(tempLoc);
                    CustomBlock.place(config.getString("config.watered-pot"), tempLoc);
                    return null;
                });
            }
        }
    }
}
