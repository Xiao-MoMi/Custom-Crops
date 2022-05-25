package net.momirealms.customcrops.Crops;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import net.momirealms.customcrops.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;

public class TimeCheck extends BukkitRunnable {

    FileConfiguration config = CustomCrops.instance.getConfig();
    List<Long> cropGrowTimeList = config.getLongList("config.grow-time");
    List<Long> sprinklerWorkTimeList = config.getLongList("config.sprinkler-time");
    BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

    @Override
    public void run() {
        long time = Objects.requireNonNull(Bukkit.getWorld("world")).getTime();

        cropGrowTimeList.forEach(cropGrowTime -> {

            if(time == cropGrowTime){
                bukkitScheduler.runTaskAsynchronously(CustomCrops.instance,()->{
                    long start1 = System.currentTimeMillis();
                    CropManager.cleanLoadedCache();
                    long finish1 = System.currentTimeMillis();
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f农作物缓存清理耗时&a" + String.valueOf(finish1-start1) + "&fms（异步）",Bukkit.getConsoleSender());
                    long start2 = System.currentTimeMillis();
                    CropManager.saveData();
                    long finish2 = System.currentTimeMillis();
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f农作物数据保存耗时&a" + String.valueOf(finish2-start2) + "&fms（异步）",Bukkit.getConsoleSender());
                    long start3 = System.currentTimeMillis();
                    CropGrow.cropGrow();
                    long finish3 = System.currentTimeMillis();
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f农作物生长耗时&a" + String.valueOf(finish3-start3) + "&fms（部分异步）",Bukkit.getConsoleSender());
                });
            }
        });

        sprinklerWorkTimeList.forEach(sprinklerTime -> {

            if(time == sprinklerTime){
                bukkitScheduler.runTaskAsynchronously(CustomCrops.instance,()->{
                    long start1 = System.currentTimeMillis();
                    SprinklerManager.cleanCache();
                    long finish1 = System.currentTimeMillis();
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f洒水器缓存清理耗时&a" + String.valueOf(finish1-start1) + "&fms（部分异步）",Bukkit.getConsoleSender());
                    long start2 = System.currentTimeMillis();
                    SprinklerManager.saveData();
                    long finish2 = System.currentTimeMillis();
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f洒水器数据保存耗时&a" + String.valueOf(finish2-start2) + "&fms（异步）",Bukkit.getConsoleSender());
                    long start3 = System.currentTimeMillis();
                    SprinklerWork.sprinklerWork();
                    long finish3 = System.currentTimeMillis();
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f洒水器工作耗时&a" + String.valueOf(finish3-start3) + "&fms（部分异步）",Bukkit.getConsoleSender());
                });
            }
        });
    }
}
