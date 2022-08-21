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
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
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

    public static YamlConfiguration data;
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
        File file = new File(CustomCrops.plugin.getDataFolder(), "data" + File.separator + "sprinkler.yml");
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomCrops] 洒水器数据文件生成失败!</red>");
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 保存数据
     */
    public void saveData(){
        File file = new File(CustomCrops.plugin.getDataFolder(), "data" + File.separator + "sprinkler.yml");
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
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))) {
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (value instanceof MemorySection map){
                            String[] coordinate = StringUtils.split(key, ",");
                            Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                            bukkitScheduler.runTask(CustomCrops.plugin, ()->{
                                int water = (int) map.get("water");
                                int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                                if(!FurnitureUtil.isSprinkler(location)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                    return;
                                }
                                if (water > 0){
                                    data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                    bukkitScheduler.runTaskLater(CustomCrops.plugin, ()-> {
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
    }

    /**
     * 洒水器工作，Mode 2
     * @param worldName 世界名
     */
    public void workModeTwo(String worldName){
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
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
                        bukkitScheduler.runTask(CustomCrops.plugin, ()->{
                            int water = (int) map.get("water");
                            int range = (int) Optional.ofNullable(map.get("range")).orElse(0);
                            if (water > 0){
                                data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                bukkitScheduler.runTaskLater(CustomCrops.plugin, ()-> {
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
            });
        }
    }

    /**
     * 洒水器工作，Mode 3
     * @param worldName 世界名
     */
    public void workModeThree(String worldName){
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
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
                            bukkitScheduler.runTask(CustomCrops.plugin, ()->{
                                if(!FurnitureUtil.isSprinkler(location)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                    return;
                                }
                                if (water > 0){
                                    data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                    bukkitScheduler.runTaskLater(CustomCrops.plugin, ()-> {
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
                                bukkitScheduler.runTaskLater(CustomCrops.plugin, ()-> {
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
    }

    /**
     * 洒水器工作，Mode 4
     * @param worldName 世界名
     */
    public void workModeFour(String worldName){
        if(!ConfigReader.Config.allWorld) updateData();
        if(!ConfigReader.Config.allWorld) saveData();
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
                            bukkitScheduler.runTaskLater(CustomCrops.plugin, ()-> {
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
    }


    /**
     * 所有世界的洒水器工作
     */
    public void sprinklerWorkAll(){
        updateData();
        List<World> worlds = Bukkit.getWorlds();
        for (int i = 0; i < worlds.size(); i++){
            String worldName = worlds.get(i).getName();
            bukkitScheduler.runTaskLaterAsynchronously(CustomCrops.plugin, () -> {
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


    /**
     * 获取某个洒水器的水量
     * @param location 洒水器位置
     * @param world 世界
     * @param x 坐标
     * @param z 坐标
     * @param sprinkler 洒水器类型
     * @return 水量
     */
    public static int getCurrentWater(Location location, String world, int x, int z, Sprinkler sprinkler) {
        int currentWater;
        if (sprinkler != null) currentWater = sprinkler.getWater();
        else {
            String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z + ".water";
            currentWater = data.getInt(path);
        }
        return currentWater;
    }
}
