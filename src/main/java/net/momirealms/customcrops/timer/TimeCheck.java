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
import org.bukkit.scheduler.BukkitRunnable;

public class TimeCheck extends BukkitRunnable {

    @Override
    public void run() {
        ConfigReader.Config.worlds.forEach(world ->{
            long time = world.getTime();
            ConfigReader.Config.cropGrowTimeList.forEach(cropGrowTime -> {
                if(time == 0)
                    if(ConfigReader.Season.enable && ConfigReader.Season.seasonChange)
                        CustomCrops.plugin.getSeasonManager().getSeason(world);
                if(time == cropGrowTime){
                    if (ConfigReader.Config.allWorld){
                        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
                            CustomCrops.plugin.getCropManager().cropGrowAll();
                        });
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomCrops.plugin, ()->{
                            CustomCrops.plugin.getSprinklerManager().sprinklerWorkAll();
                        }, ConfigReader.Config.timeToGrow);
                    }else {
                        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
                            switch (ConfigReader.Config.growMode){
                                case 1 -> CustomCrops.plugin.getCropManager().growModeOne(world.getName());
                                case 2 -> CustomCrops.plugin.getCropManager().growModeTwo(world.getName());
                                case 3 -> CustomCrops.plugin.getCropManager().growModeThree(world.getName());
                                case 4 -> CustomCrops.plugin.getCropManager().growModeFour(world.getName());
                            }
                        });
                        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomCrops.plugin, ()->{
                            switch (ConfigReader.Config.growMode){
                                case 1 -> CustomCrops.plugin.getSprinklerManager().workModeOne(world.getName());
                                case 2 -> CustomCrops.plugin.getSprinklerManager().workModeTwo(world.getName());
                                case 3 -> CustomCrops.plugin.getSprinklerManager().workModeThree(world.getName());
                                case 4 -> CustomCrops.plugin.getSprinklerManager().workModeFour(world.getName());
                            }
                        }, ConfigReader.Config.timeToGrow);
                    }
                }
            });
        });
    }
}