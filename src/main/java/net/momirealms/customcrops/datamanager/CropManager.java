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
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.utils.AdventureManager;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.RetainingSoil;
import net.momirealms.customcrops.fertilizer.SpeedGrow;
import net.momirealms.customcrops.utils.CropInstance;
import net.momirealms.customcrops.utils.SimpleLocation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class CropManager {

    private YamlConfiguration data;
    public static ConcurrentHashMap<Location, String> Cache = new ConcurrentHashMap<>();
    private final BukkitScheduler bukkitScheduler;

    public CropManager(){
        this.bukkitScheduler = Bukkit.getScheduler();
    }

    /**
     * 载入数据
     */
    public void loadData() {
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "crop.yml");
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomCrops] 农作物数据文件生成失败!</red>");
            }
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 保存数据
     */
    public void saveData() {
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "crop.yml");
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomCrops] crop.yml保存出错!</red>");
        }
    }

    /**
     * 将hashmap中的数据保存到data中
     */
    public void updateData(){
        Cache.forEach((location, String) -> {
            int x = location.getBlockX();
            int z = location.getBlockZ();
            data.set(location.getWorld().getName() + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z, String);
        });
        Cache.clear();
    }

    /**
     * 清除无用数据
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
     * 农作物生长
     * @param worldName 进行生长判定的世界名
     */
    public void cropGrow(String worldName){

        Long time1 = System.currentTimeMillis();
        updateData();
        Long time2 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 农作物数据更新" + (time2-time1) + "ms");
        }
        if (data.contains(worldName)){
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                World world = Bukkit.getWorld(worldName);
                if (ConfigReader.Config.onlyLoadedGrow || world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        CustomBlock seedBlock = CustomBlock.byAlreadyPlaced(seedLocation.getBlock());
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(worldName).append(".").append(chunk).append(".").append(key);
                        if(seedBlock == null) {
                            data.set(stringBuilder.toString(), null);
                            return;
                        }
                        String namespacedID = seedBlock.getNamespacedID();
                        String id = seedBlock.getId();
                        if(namespacedID.equals(ConfigReader.Basic.dead)) {
                            data.set(stringBuilder.toString(), null);
                            return;
                        }
                        if(!namespacedID.contains("_stage_")) {
                            data.set(stringBuilder.toString(), null);
                            return;
                        }
                        Location potLocation = seedLocation.clone().subtract(0,1,0);
                        CustomBlock pot = CustomBlock.byAlreadyPlaced(potLocation.getBlock());
                        String potNamespacedID = pot.getNamespacedID();
                        String[] cropNameList = StringUtils.split(id,"_");
                        CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                        if (pot == null || cropInstance == null){
                            data.set(stringBuilder.toString(), null);
                            return;
                        }
                        int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                        if (potNamespacedID.equals(ConfigReader.Basic.watered_pot)){
                            //如果启用季节限制且农作物有季节需求
                            if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                                if (isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){
                                    data.set(stringBuilder.toString(), null);
                                    bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                                        CustomBlock.remove(seedLocation);
                                        CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                                    }, random);
                                    return;
                                }
                            }
                            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                            if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) != null && cropInstance.getGrowChance() > Math.random()) {
                                Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(potLocation));
                                if (fertilizer != null){
                                    int times = fertilizer.getTimes();
                                    if (times > 0){
                                        fertilizer.setTimes(times - 1);
                                        if (fertilizer instanceof SpeedGrow speedGrow){
                                            if (Math.random() < speedGrow.getChance() && CustomBlock.getInstance(StringUtils.chop(namespacedID) + (nextStage + 1)) != null){
                                                addStage(potLocation, seedLocation, namespacedID, nextStage + 1, random);
                                            }else {
                                                addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                            }
                                        }else if(fertilizer instanceof RetainingSoil retainingSoil){
                                            if (Math.random() < retainingSoil.getChance()){
                                                addStage(seedLocation, namespacedID, nextStage, random);
                                            }else {
                                                addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                            }
                                        }else if(fertilizer instanceof QualityCrop){
                                            addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                        }else {
                                            AdventureManager.consoleMessage("<red>[CustomCrops] 发现未知类型肥料,已自动清除错误数据!</red>");
                                            PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));
                                        }
                                    }else {
                                        PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));
                                    }
                                }
                                else {
                                    addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                }
                            }
                            else if(cropInstance.getGiant() != null){
                                bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
                                    CustomBlock.remove(potLocation);
                                    CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                    if(cropInstance.getGiantChance() > Math.random()){
                                        data.set(stringBuilder.toString(), null);
                                        CustomBlock.remove(seedLocation);
                                        CustomBlock.place(cropInstance.getGiant(), seedLocation);
                                    }
                                }, random);
                            }else {
                                if (!ConfigReader.Season.enable) data.set(stringBuilder.toString(), null);
                                bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                                    CustomBlock.remove(potLocation);
                                    CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                }, random);
                            }
                        }else if(potNamespacedID.equals(ConfigReader.Basic.pot)){
                            if(!ConfigReader.Season.enable || cropInstance.getSeasons() == null) return;
                            if(isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){
                                data.set(stringBuilder.toString(), null);
                                bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                                }, random);
                            }
                        }else {
                            data.set(stringBuilder.toString(), null);
                        }
                    });
                }
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 农作物生长过程" + (time3-time2) + "ms");
        }
        saveData();
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 农作物数据保存" + (time4-time3) + "ms");
        }
    }

    /**
     * 全部世界农作物生长
     * 对于使用动态加载世界的服务器有效
     */
    public void cropGrowAll(){
        Long time1 = System.currentTimeMillis();
        updateData();
        Long time2 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 农作物数据更新" + (time2-time1) + "ms");
        }
        Bukkit.getWorlds().forEach(world -> {
            String worldName = world.getName();
            if (data.contains(worldName)){
                data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                    String[] split = StringUtils.split(chunk,",");
                    if (ConfigReader.Config.onlyLoadedGrow || world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                        data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                            String[] coordinate = StringUtils.split(key, ",");
                            Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                            CustomBlock seedBlock = CustomBlock.byAlreadyPlaced(seedLocation.getBlock());
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(worldName).append(".").append(chunk).append(".").append(key);
                            if(seedBlock == null) {
                                data.set(stringBuilder.toString(), null);
                                return;
                            }
                            String namespacedID = seedBlock.getNamespacedID();
                            String id = seedBlock.getId();
                            if(namespacedID.equals(ConfigReader.Basic.dead)) {
                                data.set(stringBuilder.toString(), null);
                                return;
                            }
                            if(!namespacedID.contains("_stage_")) {
                                data.set(stringBuilder.toString(), null);
                                return;
                            }
                            Location potLocation = seedLocation.clone().subtract(0,1,0);
                            CustomBlock pot = CustomBlock.byAlreadyPlaced(potLocation.getBlock());
                            String potNamespacedID = pot.getNamespacedID();
                            String[] cropNameList = StringUtils.split(id,"_");
                            CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                            if (pot == null || cropInstance == null){
                                data.set(stringBuilder.toString(), null);
                                return;
                            }
                            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
                            if (potNamespacedID.equals(ConfigReader.Basic.watered_pot)){
                                if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                                    if (isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){
                                        data.set(stringBuilder.toString(), null);
                                        bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                                            CustomBlock.remove(seedLocation);
                                            CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                                        }, random);
                                        return;
                                    }
                                }
                                int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                                if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) != null  && cropInstance.getGrowChance() > Math.random()) {
                                    Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(potLocation));
                                    if (fertilizer != null){
                                        int times = fertilizer.getTimes();
                                        if (times > 0){
                                            fertilizer.setTimes(times - 1);
                                            if (fertilizer instanceof SpeedGrow speedGrow){
                                                if (Math.random() < speedGrow.getChance() && CustomBlock.getInstance(StringUtils.chop(namespacedID) + (nextStage + 1)) != null){
                                                    addStage(potLocation, seedLocation, namespacedID, nextStage + 1, random);
                                                }else {
                                                    addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                                }
                                            }else if(fertilizer instanceof RetainingSoil retainingSoil){
                                                if (Math.random() < retainingSoil.getChance()){
                                                    addStage(seedLocation, namespacedID, nextStage, random);
                                                }else {
                                                    addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                                }
                                            }else if(fertilizer instanceof QualityCrop){
                                                addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                            }else {
                                                AdventureManager.consoleMessage("<red>[CustomCrops] 发现未知类型肥料,已自动清除错误数据!</red>");
                                                PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));
                                            }
                                        }else {
                                            PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));
                                        }
                                    }
                                    else {
                                        addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                    }
                                }
                                else if(cropInstance.getGiant() != null){
                                    bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
                                        CustomBlock.remove(potLocation);
                                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                        if(cropInstance.getGiantChance() > Math.random()){
                                            data.set(stringBuilder.toString(), null);
                                            CustomBlock.remove(seedLocation);
                                            CustomBlock.place(cropInstance.getGiant(), seedLocation);
                                        }
                                    }, random);
                                }else {
                                    if (!ConfigReader.Season.enable) data.set(stringBuilder.toString(), null);
                                    bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                                        CustomBlock.remove(potLocation);
                                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                                    }, random);
                                }
                            }else if(potNamespacedID.equals(ConfigReader.Basic.pot)){
                                if(!ConfigReader.Season.enable || cropInstance.getSeasons() == null) return;
                                if(isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){
                                    data.set(stringBuilder.toString(), null);
                                    bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                                        CustomBlock.remove(seedLocation);
                                        CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                                    }, random);
                                }
                            }else {
                                data.set(stringBuilder.toString(), null);
                            }
                        });
                    }
                });
            }
        });
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 农作物生长过程" + (time3-time2) + "ms");
        }
        saveData();
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 农作物数据保存" + (time4-time3) + "ms");
        }
    }

    /**
     * 判定季节
     * @param worldName 世界名
     * @param seasons 农作物能生长的季节
     * @param seedLocation 农作物的位置
     */
    private boolean isWrongSeason(Location seedLocation, List<String> seasons, String worldName){
        if(ConfigReader.Season.greenhouse){
            for(int i = 1; i <= ConfigReader.Season.range; i++){
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(seedLocation.clone().add(0,i,0).getBlock());
                if (customBlock != null){
                    if(customBlock.getNamespacedID().equals(ConfigReader.Basic.glass)){
                        return false;
                    }
                }
            }
        }
        if (!ConfigReader.Config.allWorld){
            for(String season : seasons){
                if (season.equals(SeasonManager.SEASON.get(worldName))) {
                    return false;
                }
            }
        }else {
            for(String season : seasons){
                if (season.equals(SeasonManager.SEASON.get(ConfigReader.Config.referenceWorld))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 生长一个阶段(消耗水)
     * @param potLocation 种植盆位置
     * @param seedLocation 农作物位置
     * @param namespacedID 农作物下一阶段的ID
     * @param nextStage 农作物下一阶段的阶段数
     * @param random 随机生长时间
     */
    private void addStage(Location potLocation, Location seedLocation, String namespacedID, int nextStage, int random){
        String stage = StringUtils.chop(namespacedID) + nextStage;
        bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
            CustomBlock.remove(potLocation);
            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
            CustomBlock.remove(seedLocation);
            CustomBlock.place(stage, seedLocation);
        }, random);
    }

    /**
     * 生长一个阶段(不消耗水)
     * @param seedLocation 农作物位置
     * @param namespacedID 农作物下一阶段的ID
     * @param nextStage 农作物下一阶段的阶段数
     * @param random 随机生长时间
     */
    private void addStage(Location seedLocation, String namespacedID, int nextStage, int random){
        String stage = StringUtils.chop(namespacedID) + nextStage;
        bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
            CustomBlock.remove(seedLocation);
            CustomBlock.place(stage, seedLocation);
        }, random);
    }
}