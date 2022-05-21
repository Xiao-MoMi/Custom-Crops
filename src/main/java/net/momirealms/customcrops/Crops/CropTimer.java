package net.momirealms.customcrops.Crops;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CropTimer {

    int taskID;

    public static void stopTimer(int ID) {
        Bukkit.getScheduler().cancelTask(ID);
        Bukkit.getServer().getScheduler().cancelTask(ID);
    }

    public CropTimer() {
        TimeCheck tc = new TimeCheck();
        BukkitTask task = tc.runTaskTimerAsynchronously(CustomCrops.instance, 1,1);
        this.taskID = task.getTaskId();
    }

    public int getTaskID() {
        return this.taskID;
    }
}
