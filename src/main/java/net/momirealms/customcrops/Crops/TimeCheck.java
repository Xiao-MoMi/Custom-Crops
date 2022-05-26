package net.momirealms.customcrops.Crops;

import net.momirealms.customcrops.ConfigManager;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class TimeCheck extends BukkitRunnable {

    BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

    @Override
    public void run() {
        long time = Objects.requireNonNull(Bukkit.getWorld("world")).getTime();

        ConfigManager.Config.cropGrowTimeList.forEach(cropGrowTime -> {
            if(time == cropGrowTime){
                bukkitScheduler.runTaskAsynchronously(CustomCrops.instance, CropManager::CropGrow);
            }
        });
        ConfigManager.Config.sprinklerWorkTimeList.forEach(sprinklerTime -> {
            if(time == sprinklerTime){
                bukkitScheduler.runTaskAsynchronously(CustomCrops.instance, SprinklerManager::SprinklerWork);
            }
        });
    }
}