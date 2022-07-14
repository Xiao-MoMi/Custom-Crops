package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.utils.AdventureManager;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.RetainingSoil;
import net.momirealms.customcrops.fertilizer.SpeedGrow;
import net.momirealms.customcrops.utils.CropInstance;
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
    private final CustomCrops plugin;
    public static ConcurrentHashMap<Location, String> Cache = new ConcurrentHashMap<>();
    private BukkitScheduler bukkitScheduler;

    public CropManager(CustomCrops plugin){
        this.plugin = plugin;
        this.bukkitScheduler = Bukkit.getScheduler();
    }

    //载入数据
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

    //保存数据
    public void saveData() {
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "crop.yml");
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomCrops] crop.yml保存出错!</red>");
        }
    }

    //将缓存内新数据更新到data内
    public void updateData(){
        Cache.forEach((location, String) -> {
            int x = location.getBlockX();
            int z = location.getBlockZ();
            data.set(location.getWorld().getName() + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z, String);
        });
        Cache.clear();
    }

    //隐藏指令，清除无用数据
    public void cleanData(){
        data.getKeys(false).forEach(world -> {
            data.getConfigurationSection(world).getKeys(false).forEach(chunk ->{
                if (data.getConfigurationSection(world + "." + chunk).getKeys(false).size() == 0){
                    data.set(world + "." + chunk, null);
                }
            });
        });
    }

    //农作物生长
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
                        if (pot == null) return;
                        String potNamespacedID = pot.getNamespacedID();
                        String[] cropNameList = StringUtils.split(id,"_");
                        CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
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
                            if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) != null) {
                                Fertilizer fertilizer = PotManager.Cache.get(potLocation);
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
                                            PotManager.Cache.remove(potLocation);
                                        }
                                    }else {
                                        PotManager.Cache.remove(potLocation);
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
                                if (cropInstance.getReturnStage() == null && !ConfigReader.Season.enable) data.set(stringBuilder.toString(), null);
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

    //农作物生长
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
                            if (pot == null) return;
                            String potNamespacedID = pot.getNamespacedID();
                            String[] cropNameList = StringUtils.split(id,"_");
                            CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
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
                                if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) != null) {
                                    Fertilizer fertilizer = PotManager.Cache.get(potLocation);
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
                                                PotManager.Cache.remove(potLocation);
                                            }
                                        }else {
                                            PotManager.Cache.remove(potLocation);
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
                                    if (cropInstance.getReturnStage() == null && !ConfigReader.Season.enable) data.set(stringBuilder.toString(), null);
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
        for(String season : seasons){
            if (season.equals(SeasonManager.SEASON.get(worldName))) {
                return false;
            }
        }
        return true;
    }

    private void addStage(Location potLocation, Location seedLocation, String namespacedID, int nextStage, int random){
        bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
            CustomBlock.remove(potLocation);
            CustomBlock.place(ConfigReader.Basic.pot, potLocation);
            CustomBlock.remove(seedLocation);
            CustomBlock.place(StringUtils.chop(namespacedID) + nextStage, seedLocation);
        }, random);
    }

    private void addStage(Location seedLocation, String namespacedID, int nextStage, int random){
        bukkitScheduler.runTaskLater(CustomCrops.instance, () ->{
            CustomBlock.remove(seedLocation);
            CustomBlock.place(StringUtils.chop(namespacedID) + nextStage, seedLocation);
        }, random);
    }
}