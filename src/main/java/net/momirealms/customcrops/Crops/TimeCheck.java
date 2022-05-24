package net.momirealms.customcrops.Crops;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class TimeCheck extends BukkitRunnable {

    FileConfiguration config = CustomCrops.instance.getConfig();
    List<Long> cropGrowTimeList = config.getLongList("config.grow-time");
    List<Long> sprinklerWorkTimeList = config.getLongList("config.sprinkler-time");

    @Override
    public void run() {
        long time = Objects.requireNonNull(Bukkit.getWorld("world")).getTime();

        cropGrowTimeList.forEach(cropGrowTime -> {

            if(time == cropGrowTime){
                CropManager.cleanLoadedCache();
            }
            if(time == (cropGrowTime + 50)){
                CropManager.saveData();
            }
            if(time == (cropGrowTime + 100)){
                CropGrow.cropGrow();
            }
        });

        sprinklerWorkTimeList.forEach(sprinklerTime -> {

            if(time == sprinklerTime){
                SprinklerManager.cleanCache();
            }
            if(time == (sprinklerTime + 50)){
                SprinklerManager.saveData();
            }
            if(time == (sprinklerTime + 100)){
                SprinklerWork.sprinklerWork();
            }
        });
    }
}
