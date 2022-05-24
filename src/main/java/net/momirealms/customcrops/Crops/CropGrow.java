package net.momirealms.customcrops.Crops;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class CropGrow {
    public static void cropGrow(){
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
                    String namespace = CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().split(":")[0];
                    String[] cropNameList = CustomBlock.byAlreadyPlaced(world.getBlockAt(seedLocation)).getNamespacedID().split(":")[1].split("_");
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
                    if (CustomBlock.getInstance( namespace +":"+cropNameList[0] + "_stage_" + nextStage) != null) {
                        Bukkit.getScheduler().callSyncMethod(CustomCrops.instance, () ->{
                            CustomBlock.remove(potLocation);
                            CustomBlock.place(config.getString("config.pot"),potLocation);
                            if(Math.random()< config.getDouble("config.grow-success-chance")){
                                CustomBlock.remove(seedLocation);
                                CustomBlock.place(namespace + ":" + cropNameList[0] + "_stage_" + nextStage,seedLocation);
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
}
