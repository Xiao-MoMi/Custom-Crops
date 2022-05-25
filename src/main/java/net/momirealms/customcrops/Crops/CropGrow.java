package net.momirealms.customcrops.Crops;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

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
        double growChance = config.getDouble("config.grow-success-chance");
        String glass = config.getString("config.greenhouse-glass");
        String wateredPot = config.getString("config.watered-pot");
        String pot = config.getString("config.pot");
        String deadCrop = config.getString("config.dead-crop");
        String current = config.getString("current-season");
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

        config.getStringList("config.whitelist-worlds").forEach(worldName -> {

            World world = Bukkit.getWorld(worldName);
            CropManager.getCrops(Bukkit.getWorld(worldName)).forEach(seedLocation -> {

                Location potLocation = seedLocation.clone().subtract(0,1,0);
                Block seedBlock = world.getBlockAt(seedLocation);
                Block potBlock = world.getBlockAt(potLocation);

                String[] seasons = StringUtils.split(data.getString(worldName + "." + seedLocation.getBlockX() + "," + seedLocation.getBlockY() + "," + seedLocation.getBlockZ()),",");

                if (CustomBlock.byAlreadyPlaced(potBlock) != null && CustomBlock.byAlreadyPlaced(seedBlock) != null){

                    String seedNamespace = CustomBlock.byAlreadyPlaced(seedBlock).getNamespacedID();

                    if (CustomBlock.byAlreadyPlaced(potBlock).getNamespacedID().equalsIgnoreCase(wateredPot) && seedNamespace.contains("stage")){
                        if (seedNamespace.equalsIgnoreCase(deadCrop)){
                            return;
                        }

                        String[] split = StringUtils.split(seedNamespace,":");
                        String[] cropNameList = StringUtils.split(split[1],"_");

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
                                if (Objects.equals(season, current)) {
                                    wrongSeason = false;
                                    break;
                                }
                            }
                            if(wrongSeason){
                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(deadCrop,seedLocation);
                                    return null;
                                });
                                return;
                            }
                        }
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance( split[0] +":"+cropNameList[0] + "_stage_" + nextStage) != null) {
                            bukkitScheduler.callSyncMethod(CustomCrops.instance, () ->{
                                CustomBlock.remove(potLocation);
                                CustomBlock.place(pot,potLocation);
                                if(Math.random()< growChance){
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage,seedLocation);
                                }
                                return null;
                            });
                        }
                    }else if(CustomBlock.byAlreadyPlaced(potBlock).getNamespacedID().equalsIgnoreCase(pot) && seedNamespace.contains("stage")){
                        if (seedNamespace.equalsIgnoreCase(deadCrop)){
                            return;
                        }
                        if(enable_season) {
                            if(enable_greenhouse){
                                for(int i = 1; i <= range; i++){
                                    Location tempLocation = seedLocation.clone().add(0,i,0);
                                    if (CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)) != null){
                                        if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)).getNamespacedID().equalsIgnoreCase(glass)){
                                            return;
                                        }
                                    }
                                }
                            }
                            boolean wrongSeason = true;
                            for (String season : seasons) {
                                if (Objects.equals(season, current)) {
                                    wrongSeason = false;
                                    break;
                                }
                            }
                            if (wrongSeason) {
                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(deadCrop, seedLocation);
                                    return null;
                                });
                            }
                        }
                    }
                }
            });
        });
    }
}
