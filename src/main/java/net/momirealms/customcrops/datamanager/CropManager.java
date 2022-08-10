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
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.listener.JoinAndQuit;
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
import java.util.HashSet;
import java.util.List;
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
     * 将数据保存到data中
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
     * 农作物生长，Mode 1
     * 仅加载区块生长
     * @param worldName 进行生长判定的世界名
     */
    public void growModeOne(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据更新" + (time2-time1) + "ms");
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        if (growJudge(worldName, seedLocation)){
                            data.set(worldName + "." + chunk + "." + key, null);
                        }
                    });
                }
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物生长过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据保存" + (time4-time3) + "ms");
    }

    /**
     * 农作物生长，Mode 2
     * 仅在线玩家的农作物生长
     * @param worldName 进行生长判定的世界名
     */
    public void growModeTwo(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据更新" + (time2-time1) + "ms");
        HashSet<String> players = new HashSet<>(JoinAndQuit.onlinePlayers);
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                    if (!players.contains(value)) return;
                    String[] coordinate = StringUtils.split(key, ",");
                    Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                    if (growJudge(worldName, seedLocation)){
                        data.set(worldName + "." + chunk + "." + key, null);
                    }
                });
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物生长过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据保存" + (time4-time3) + "ms");
    }

    /**
     * 农作物生长，Mode 3
     * 仅在线玩家和加载区块的农作物生长
     * @param worldName 进行生长判定的世界名
     */
    public void growModeThree(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据更新" + (time2-time1) + "ms");
        HashSet<String> players = new HashSet<>(JoinAndQuit.onlinePlayers);
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                //区块被加载，则强行生长判定
                if (world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))){
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        if (growJudge(worldName, seedLocation)){
                            data.set(worldName + "." + chunk + "." + key, null);
                        }
                    });
                }
                //区块未加载，玩家在线
                else{
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        if (!players.contains(value)) return;
                        String[] coordinate = StringUtils.split(key, ",");
                        Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        if (growJudge(worldName, seedLocation)){
                            data.set(worldName + "." + chunk + "." + key, null);
                        }
                    });
                }
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物生长过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据保存" + (time4-time3) + "ms");
    }

    /**
     * 农作物生长，Mode 4
     * 全部农作物生长
     * @param worldName 进行生长判定的世界名
     */
    public void growModeFour(String worldName){
        Long time1 = System.currentTimeMillis();
        if(!ConfigReader.Config.allWorld){
            updateData();
        }
        Long time2 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据更新" + (time2-time1) + "ms");
        if (data.contains(worldName)){
            World world = Bukkit.getWorld(worldName);
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                    String[] coordinate = StringUtils.split(key, ",");
                    Location seedLocation = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                    if (growJudge(worldName, seedLocation)){
                        data.set(worldName + "." + chunk + "." + key, null);
                    }
                });
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物生长过程" + (time3-time2) + "ms");
        if(!ConfigReader.Config.allWorld){
            saveData();
        }
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime) AdventureManager.consoleMessage("性能监测: 农作物数据保存" + (time4-time3) + "ms");
    }

    /**
     * 全部世界农作物生长
     * 对于使用动态加载世界的服务器有效
     */
    public void cropGrowAll(){
        updateData();
        List<World> worlds = Bukkit.getWorlds();
        for (int i = 0; i < worlds.size(); i++){
            String worldName = worlds.get(i).getName();
            bukkitScheduler.runTaskLaterAsynchronously(CustomCrops.instance, () -> {
                switch (ConfigReader.Config.growMode){
                    case 1 -> growModeOne(worldName);
                    case 2 -> growModeTwo(worldName);
                    case 3 -> growModeThree(worldName);
                    case 4 -> growModeFour(worldName);
                }
            }, i * 40L);
        }
        saveData();
    }

    /**
     * 对某个位置进行生长判定
     * @param worldName 世界名
     * @param seedLocation 种子位置
     * @return 是否消除数据
     */
    private boolean growJudge(String worldName, Location seedLocation) {
        CustomBlock seedBlock = CustomBlock.byAlreadyPlaced(seedLocation.getBlock());
        //自定义农作物方块不存在
        if(seedBlock == null) {
            return true;
        }
        String namespacedID = seedBlock.getNamespacedID();
        String id = seedBlock.getId();
        //已死亡或不是农作物
        if(namespacedID.equals(ConfigReader.Basic.dead) || !namespacedID.contains("_stage_")) {
            return true;
        }
        //农作物下方自定义方块不存在
        Location potLocation = seedLocation.clone().subtract(0,1,0);
        CustomBlock pot = CustomBlock.byAlreadyPlaced(potLocation.getBlock());
        if (pot == null){
            return true;
        }
        //农作物实例不存在
        String[] cropNameList = StringUtils.split(id,"_");
        CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
        if (cropInstance == null){
            return true;
        }
        //获取自定义方块ID
        String potNamespacedID = pot.getNamespacedID();
        if (potNamespacedID.equals(ConfigReader.Basic.watered_pot)){
            int random = new Random().nextInt(ConfigReader.Config.timeToGrow);
            //如果启用季节限制且农作物有季节需求
            if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                if (isWrongSeason(seedLocation, cropInstance.getSeasons(), worldName)){

                    bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                        CustomBlock.remove(seedLocation);
                        CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                    }, random);
                    return true;

                }
            }
            //获取下一阶段
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            //下一阶段存在
            if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) != null) {
                //尝试获取肥料类型
                Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(potLocation));
                //有肥料
                if (fertilizer != null){
                    //查询剩余使用次数
                    int times = fertilizer.getTimes();
                    if (times > 0){
                        fertilizer.setTimes(times - 1);
                        //生长激素
                        if (fertilizer instanceof SpeedGrow speedGrow){
                            if (cropInstance.getGrowChance() > Math.random()){
                                //农作物存在下两个阶段
                                if (Math.random() < speedGrow.getChance() && CustomBlock.getInstance(StringUtils.chop(namespacedID) + (nextStage + 1)) != null){
                                    addStage(potLocation, seedLocation, namespacedID, nextStage + 1, random);
                                }else {
                                    addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                }
                            }else {
                                notAddStage(potLocation, random);
                            }
                        }
                        //保湿土壤
                        else if(fertilizer instanceof RetainingSoil retainingSoil){
                            if (Math.random() < retainingSoil.getChance()){
                                if (cropInstance.getGrowChance() > Math.random()){
                                    addStage(seedLocation, namespacedID, nextStage, random);
                                }
                            }else {
                                if (cropInstance.getGrowChance() > Math.random()){
                                    addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                                }else {
                                    notAddStage(potLocation, random);
                                }
                            }
                        }
                        //品质肥料
                        else if(fertilizer instanceof QualityCrop){
                            if (cropInstance.getGrowChance() > Math.random()){
                                addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                            }else {
                                notAddStage(potLocation, random);
                            }
                        }else {
                            //未知肥料类型处理
                            AdventureManager.consoleMessage("<red>[CustomCrops] Unknown fertilizer, Auto removed!</red>");
                            PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));

                            if (cropInstance.getGrowChance() > Math.random()){
                                addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                            }else {
                                notAddStage(potLocation, random);
                            }
                        }
                        //肥料的最后一次使用
                        if (times == 1){
                            PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));
                        }
                    }
                    else {
                        //移除肥料信息，一般不会出现此情况
                        PotManager.Cache.remove(SimpleLocation.fromLocation(potLocation));
                        if (cropInstance.getGrowChance() > Math.random()){
                            addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                        }else {
                            notAddStage(potLocation, random);
                        }
                    }
                }
                //没有肥料
                else {
                    if (cropInstance.getGrowChance() > Math.random()){
                        addStage(potLocation, seedLocation, namespacedID, nextStage, random);
                    }else {
                        notAddStage(potLocation, random);
                    }
                }
            }
            //农作物是否存在巨大化
            else if(cropInstance.getGiant() != null){
                //巨大化判定
                if (cropInstance.getGiantChance() > Math.random()){
                    //成功巨大化，移除数据
                    bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
                        CustomBlock.remove(seedLocation);
                        CustomBlock.place(cropInstance.getGiant(), seedLocation);
                        CustomBlock.remove(potLocation);
                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                    }, random);
                    return true;
                }else {
                    //失败，转湿为干
                    bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
                        CustomBlock.remove(potLocation);
                        CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                    }, random);
                    return ConfigReader.Config.growMode == 4;
                }
            }else {
                //若无下一阶段，无巨大化，未启用季节，则移除无用数据
                if (!ConfigReader.Season.enable) return true;
                bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                    CustomBlock.remove(potLocation);
                    CustomBlock.place(ConfigReader.Basic.pot, potLocation);
                }, random);
            }
        }
        //干燥的种植盆
        else if(potNamespacedID.equals(ConfigReader.Basic.pot)){
            //未启用季节
            if(!ConfigReader.Season.enable) return false;
            //农作物无视季节
            List<String> seasons = cropInstance.getSeasons();
            if(seasons == null) return false;
            //错误季节
            if(isWrongSeason(seedLocation, seasons, worldName)){
                bukkitScheduler.runTaskLater(CustomCrops.instance, () -> {
                    CustomBlock.remove(seedLocation);
                    CustomBlock.place(ConfigReader.Basic.dead, seedLocation);
                }, new Random().nextInt(ConfigReader.Config.timeToGrow));
                return true;
            }
        }
        //不是种植盆
        else {
            return true;
        }
        return false;
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

    /**
     * 停滞阶段(消耗水)
     * @param potLocation 种植盆位置
     * @param random 随机生长时间
     */
    private void notAddStage(Location potLocation, int random){
        bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
            CustomBlock.remove(potLocation);
            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
        }, random);
    }
}