/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.timer;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CropTimer {

    private final int taskID;

    public CropTimer() {
        TimeCheck tc = new TimeCheck();
        BukkitTask task;
        if (ConfigReader.Config.asyncCheck) task = tc.runTaskTimerAsynchronously(CustomCrops.plugin, 1,1);
        else task = tc.runTaskTimer(CustomCrops.plugin, 1,1);
        this.taskID = task.getTaskId();
    }

    public void stopTimer(int ID) {
        Bukkit.getScheduler().cancelTask(ID);
    }

    public int getTaskID() {
        return this.taskID;
    }
}
