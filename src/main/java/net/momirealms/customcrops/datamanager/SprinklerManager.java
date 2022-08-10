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

package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.listener.JoinAndQuit;
import net.momirealms.customcrops.utils.*;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SprinklerManager {

    public YamlConfiguration data;
    public static ConcurrentHashMap<SimpleLocation, Sprinkler> Cache = new ConcurrentHashMap<>();
    public static HashSet<SimpleLocation> RemoveCache = new HashSet<>();
    private final BukkitScheduler bukkitScheduler;

    public SprinklerManager(){
        this.bukkitScheduler = Bukkit.getScheduler();
    }

    /**
     * 载入数据
     */
    public void loadData() {
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "sprinkler.yml");
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomCrops] 洒水器数据文件生成失败!</red>");
            }
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 保存数据
     */
    public void saveData(){
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "sprinkler.yml");
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomCrops] sprinkler.yml保存出错!</red>");
        }
    }

    /**
     * 清理无用数据
     */
    public void cleanData(){
        data.getKeys(false).forEach(world -> {
            data.getConfigurationSection(world).getKeys(false).forEach(chunk ->{
                if (data.getConfigurationSection(world + "." + chunk).getKeys(false).size() == 0){
                    data.set(world + "." + chunk, null);
                }
            });
        });
    }

    /**
     * 将数据保存到data中
     */
    public void updateData(){
        Cache.forEach((location, sprinklerData) -> {
            String world = location.getWorldName();
            int x = location.getX();
            int z = location.getZ();
            StringBuilder stringBuilder = new StringBuilder().append(world).append(".").append(x/16).append(",").append(z/16).append(".").append(x).append(",").append(location.getY()).append(",").append(z);
            data.set(stringBuilder+".range", sprinklerData.getRange());
            data.set(stringBuilder+".water", sprinklerData.getWater());
            data.set(stringBuilder+".player", Optional.ofNullable(sprinklerData.getPlayer()).orElse("none"));
        });
        Cache.clear();
        HashSet<SimpleLocation> set = new HashSet<>(RemoveCache);
        for (SimpleLocation location : set) {
            String world = location.getWorldName();
            int x = location.getX();
            int z = location.getZ();
            data.set(world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getY() + "," + z, null);
        }
        RemoveCache.clear();
    }

    /**
     * 洒水器工作，Mode 1
     * @param worldName 世界名
     */
    public void workModeOne(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if (ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据更新" + (time2-time1) + "ms");
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))) {
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (value instanceof MemorySection map){
                            String[] coordinate = StringUtils.split(key, ",");
                            Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                            bukkitScheduler.runTask(CustomCrops.instance, ()->{
                                int water = (int) map.get("water");
                                int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                                if(!IAFurniture.getFromLocation(location, world)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                    return;
                                }
                                if (water > 0){
                                    data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                    bukkitScheduler.runTaskLater(CustomCrops.instance, ()-> {
                                        for(int i = -range; i <= range; i++){
                                            for (int j = -range; j <= range; j++){
                                                waterPot(location.clone().add(i,-1,j));
                                            }
                                        }
                                    }, new Random().nextInt(ConfigReader.Config.timeToWork));
                                }
                                if (range == 0) data.set(worldName + "." + chunk + "." + key, null);
                            });
                        }
                    });
                }
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器工作过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据保存" + (time4-time3) + "ms");
    }

    /**
     * 洒水器工作，Mode 2
     * @param worldName 世界名
     */
    public void workModeTwo(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if (ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据更新" + (time2-time1) + "ms");
        //HashSet<String> players = new HashSet<>(JoinAndQuit.onlinePlayers);
        HashSet<String> players = getPlayers();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                    if (value instanceof MemorySection map){
                        String player = (String) map.get("player");
                        if (player == null) {
                            data.set(worldName + "." + chunk + "." + key + ".player", "none");
                            return;
                        }
                        if (!players.contains(player)) return;
                        String[] coordinate = StringUtils.split(key, ",");
                        Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                        bukkitScheduler.callSyncMethod(CustomCrops.instance, ()->{
                            int water = (int) map.get("water");
                            int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                            if (water > 0){
                                data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                bukkitScheduler.runTaskLater(CustomCrops.instance, ()-> {
                                    for(int i = -range; i <= range; i++){
                                        for (int j = -range; j <= range; j++){
                                            waterPot(location.clone().add(i,-1,j));
                                        }
                                    }
                                }, new Random().nextInt(ConfigReader.Config.timeToWork));
                            }
                            if (range == 0) data.set(worldName + "." + chunk + "." + key, null);
                            return null;
                        });
                    }
                });
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器工作过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据保存" + (time4-time3) + "ms");
    }

    /**
     * 洒水器工作，Mode 3
     * @param worldName 世界名
     */
    public void workModeThree(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if (ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据更新" + (time2-time1) + "ms");
        //HashSet<String> players = new HashSet<>(JoinAndQuit.onlinePlayers);
        HashSet<String> players = getPlayers();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))) {
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (value instanceof MemorySection map){
                            int water = (int) map.get("water");
                            int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                            String[] coordinate = StringUtils.split(key, ",");
                            Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                            bukkitScheduler.runTask(CustomCrops.instance, ()->{
                                if(!IAFurniture.getFromLocation(location, world)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                    return;
                                }
                                if (water > 0){
                                    data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                    bukkitScheduler.runTaskLater(CustomCrops.instance, ()-> {
                                        for(int i = -range; i <= range; i++){
                                            for (int j = -range; j <= range; j++){
                                                waterPot(location.clone().add(i,-1,j));
                                            }
                                        }
                                    }, new Random().nextInt(ConfigReader.Config.timeToWork));
                                }
                                if (range == 0) data.set(worldName + "." + chunk + "." + key, null);
                            });
                        }
                    });
                }
                else {
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (value instanceof MemorySection map){
                            String player = (String) map.get("player");
                            if (player == null) {
                                data.set(worldName + "." + chunk + "." + key + ".player", "none");
                                return;
                            }
                            if (!players.contains(player)) return;
                            int water = (int) map.get("water");
                            int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                            if (water > 0){
                                String[] coordinate = StringUtils.split(key, ",");
                                Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                                data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                bukkitScheduler.runTaskLater(CustomCrops.instance, ()-> {
                                    for(int i = -range; i <= range; i++){
                                        for (int j = -range; j <= range; j++){
                                            waterPot(location.clone().add(i,-1,j));
                                        }
                                    }
                                }, new Random().nextInt(ConfigReader.Config.timeToWork));
                            }
                            if (range == 0) data.set(worldName + "." + chunk + "." + key, null);
                        }
                    });
                }
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器工作过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据保存" + (time4-time3) + "ms");
    }

    /**
     * 洒水器工作，Mode 4
     * @param worldName 世界名
     */
    public void workModeFour(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if (ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据更新" + (time2-time1) + "ms");
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                    if (value instanceof MemorySection map){
                        int water = (int) map.get("water");
                        int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                        if (water > 0){
                            String[] coordinate = StringUtils.split(key, ",");
                            Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                            data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                            bukkitScheduler.runTaskLater(CustomCrops.instance, ()-> {
                                for(int i = -range; i <= range; i++){
                                    for (int j = -range; j <= range; j++){
                                        waterPot(location.clone().add(i,-1,j));
                                    }
                                }
                            }, new Random().nextInt(ConfigReader.Config.timeToWork));
                        }
                        if (range == 0) data.set(worldName + "." + chunk + "." + key, null);
                    }
                });
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器工作过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 洒水器数据保存" + (time4-time3) + "ms");
    }


    /**
     * 所有世界的洒水器工作
     */
    public void sprinklerWorkAll(){
        updateData();
        List<World> worlds = Bukkit.getWorlds();
        for (int i = 0; i < worlds.size(); i++){
            String worldName = worlds.get(i).getName();
            bukkitScheduler.runTaskLaterAsynchronously(CustomCrops.instance, () -> {
                switch (ConfigReader.Config.growMode){
                    case 1 -> workModeOne(worldName);
                    case 2 -> workModeTwo(worldName);
                    case 3 -> workModeThree(worldName);
                    case 4 -> workModeFour(worldName);
                }
            }, i * 40L);
        }
        saveData();
    }

    /**
     * 转干为湿
     * @param potLoc 种植盆的位置
     */
    private void waterPot(Location potLoc) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(potLoc.getBlock());
        if(cb != null){
            if(cb.getNamespacedID().equals(ConfigReader.Basic.pot)){
                CustomBlock.remove(potLoc);
                CustomBlock.place(ConfigReader.Basic.watered_pot, potLoc);
            }
        }
    }

    private HashSet<String> getPlayers(){
        if (JedisUtil.useRedis){
            return JedisUtil.getPlayers();
        }else {
            return new HashSet<>(JoinAndQuit.onlinePlayers);
        }
    }
}
