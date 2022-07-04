package net.momirealms.customcrops.timer;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeCheck extends BukkitRunnable {

    private final CustomCrops plugin;
    public TimeCheck(CustomCrops plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ConfigReader.Config.worlds.forEach(world ->{
            long time = world.getTime();
            ConfigReader.Config.cropGrowTimeList.forEach(cropGrowTime -> {
                if(time == 0){
                    if(ConfigReader.Season.enable && ConfigReader.Season.seasonChange){
                        plugin.getSeasonManager().getSeason(world);
                    }
                }
                if(time == cropGrowTime){
                    Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.instance, () -> {
                        plugin.getCropManager().cropGrow(world.getName());
                    });
                    Bukkit.getScheduler().runTaskLaterAsynchronously(CustomCrops.instance, ()->{
                        plugin.getSprinklerManager().sprinklerWork(world.getName());
                    }, ConfigReader.Config.timeToGrow);
                }
            });
        });
    }
}