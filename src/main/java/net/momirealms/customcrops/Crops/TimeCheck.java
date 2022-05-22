package net.momirealms.customcrops.Crops;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;

public class TimeCheck extends BukkitRunnable {

    @Override
    public void run() {
        long time = Bukkit.getWorld("world").getTime();
        if(time == 23500){
            CropManager.cleanLoadedCache();
        }
        if(time == 23700){
            CropManager.saveData();
        }
        if(time == 23900){
            FileConfiguration config = CustomCrops.instance.getConfig();
            File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
            FileConfiguration data;
            data = YamlConfiguration.loadConfiguration(file);
            boolean enable_season = config.getBoolean("enable-season");
            boolean enable_greenhouse = config.getBoolean("config.enable-greenhouse");
            int range = config.getInt("config.greenhouse-range");
            String glass = config.getString("config.greenhouse-glass");
            config.getStringList("config.whitelist-worlds").forEach(worldName -> CropManager.getCrops(Objects.requireNonNull(Bukkit.getWorld(worldName))).forEach(seedLocation -> {
                World world = Bukkit.getWorld(worldName);
                Location potLocation = seedLocation.clone().subtract(0,1,0);
                String[] seasons = Objects.requireNonNull(data.getString(worldName + "." + seedLocation.getBlockX() + "," + seedLocation.getBlockY() + "," + seedLocation.getBlockZ())).split(",");
                if (CustomBlock.byAlreadyPlaced(world.getBlockAt(potLocation)) != null && CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)) != null){
                    if (CustomBlock.byAlreadyPlaced((world.getBlockAt(potLocation))).getNamespacedID().equalsIgnoreCase(config.getString("config.watered-pot")) && CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().contains("stage")){
                        if (CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.dead-crop"))){
                            return;
                        }
                        String[] cropNameList = CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().split("_");
                        Label_out:
                        if(enable_season){
                            if(enable_greenhouse){
                                for(int i = 1; i <= range; i++){
                                    Location tempLocation = seedLocation.clone().add(0,i,0);
                                    if (CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)) != null){
                                        if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)).getNamespacedID().equalsIgnoreCase(glass)){
                                            break Label_out;
                                        }
                                    }
                                }
                            }
                            boolean wrongSeason = true;
                            for(String season : seasons){
                                if(Objects.equals(season, config.getString("current-season"))){
                                    wrongSeason = false;
                                }
                            }
                            if(wrongSeason){
                                Bukkit.getScheduler().callSyncMethod(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(config.getString("config.dead-crop"),seedLocation);
                                    return null;
                                });
                                return;
                            }
                        }
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance(cropNameList[0] + "_" +cropNameList[1] +"_" + nextStage) != null) {
                            Bukkit.getScheduler().callSyncMethod(CustomCrops.instance, () ->{
                                CustomBlock.remove(potLocation);
                                CustomBlock.place(config.getString("config.pot"),potLocation);
                                if(Math.random()< config.getDouble("config.grow-success-chance")){
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(cropNameList[0] + "_" +cropNameList[1] +"_" + nextStage,seedLocation);
                                }
                                return null;
                            });
                        }
                    }else if(CustomBlock.byAlreadyPlaced((world.getBlockAt(potLocation))).getNamespacedID().equalsIgnoreCase(config.getString("config.pot")) && CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().contains("stage")){
                        if (CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.dead-crop"))){
                            return;
                        }
                        if(enable_season) {
                            if(enable_greenhouse){
                                for(int i = 1; i <= range; i++){
                                    Location tempLocation = seedLocation.clone().add(0,i,0);
                                    if (CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)) != null){
                                        if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.greenhouse-glass"))){
                                            return;
                                        }
                                    }
                                }
                            }
                            boolean wrongSeason = true;
                            for (String season : seasons) {
                                if (Objects.equals(season, config.getString("current-season"))) {
                                    wrongSeason = false;
                                }
                            }
                            if (wrongSeason) {
                                Bukkit.getScheduler().callSyncMethod(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(config.getString("config.dead-crop"), seedLocation);
                                    return null;
                                });
                            }
                        }
                    }
                }
            }));
        }
        if (time == 100){
            SprinklerManager.cleanCache();
        }
        if (time == 300){
            SprinklerManager.saveData();
        }
        if(time == 500){
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
    }
    private void waterPot(Location tempLoc, World world, FileConfiguration config) {
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
