package net.momirealms.customcrops.timer;

import net.momirealms.customcrops.datamanager.ConfigManager;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class TimeCheck extends BukkitRunnable {

    BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

    @Override
    public void run() {

        ConfigManager.Config.worlds.forEach(world ->{
            long time = Bukkit.getWorld(world).getTime();

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
        });
    }
}