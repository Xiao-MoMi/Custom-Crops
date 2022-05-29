package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.ConfigManager;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.MessageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CropManager {

    public static ConcurrentHashMap<Location, String> instances;

    //4W性能测试
    public static void testData_1(){
        for(int i = -100; i < 100; i++){
            for(int j = -100; j < 100; j++){
                Location tempLoc = new Location(Bukkit.getWorld("world"),i,100,j);
                String name = "spring";
                instances.put(tempLoc, name);
            }
        }
    }
    //20W性能测试
    public static void testData_2(){
        for(int i = -100000; i < 100000; i++){
                Location tempLoc = new Location(Bukkit.getWorld("world"),i,100,i);
                String name = "spring";
                instances.put(tempLoc, name);
        }
    }

    //开服的时候将文件的数据读入
    public CropManager(FileConfiguration data) {
        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        data = YamlConfiguration.loadConfiguration(file);
        try {
            for (String world : config.getStringList("config.whitelist-worlds")) {
                CropManager.instances = new ConcurrentHashMap<Location, String>();
                if(data.getConfigurationSection(world) != null){
                    for (String coordinate : data.getConfigurationSection(world).getKeys(false)) {
                        Location tempLocation = new Location(Bukkit.getWorld(world), (double)Integer.parseInt(coordinate.split(",")[0]), (double)Integer.parseInt(coordinate.split(",")[1]), (double)Integer.parseInt(coordinate.split(",")[2]));
                        String season = data.getString(world + "." + coordinate);
                        CropManager.instances.put(tempLocation, season);
                    }
                }
            }
        }
        catch (Exception e) {
            CropManager.instances = new ConcurrentHashMap<Location, String>();
            e.printStackTrace();
        }
        saveData();
    }
    /*
    //根据世界名获取所有的农作物
    public static List<Location> getCrops(World world){
        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        List<Location> locations = new ArrayList<Location>();
        if (config.getStringList("config.whitelist-worlds").contains(world.getName())){
            if(data.contains(world.getName())){
                data.getConfigurationSection(world.getName()).getKeys(false).forEach(key ->{
                    String[] string_list = key.split(",");
                    if (config.getBoolean("config.only-grow-in-loaded-chunks")){
                        if (world.isChunkLoaded(Integer.parseInt(string_list[0])/16, Integer.parseInt(string_list[2])/16)){
                            locations.add(new Location(world, Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2])));
                        }
                    }else {
                        locations.add(new Location(world, Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2])));
                    }
                });
            }
        }
        return locations;
    }
    */
    //保存数据
    public static void saveData(){
        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        if (CropManager.instances != null) {
            //性能更高
            Set<Map.Entry<Location, String>> en = instances.entrySet();
            for(Map.Entry<Location, String> entry : en){
                data.set(entry.getKey().getWorld().getName() + "." + entry.getKey().getBlockX() + "," + entry.getKey().getBlockY()+ ","+entry.getKey().getBlockZ(), entry.getValue());
            }
        }
        else {
            CropManager.instances = new ConcurrentHashMap<Location, String>();
            Bukkit.getConsoleSender().sendMessage("错误：空数据");
        }
        try {
            data.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("农作物数据保存出错");
        }
    }
    public static void putInstance(Location location, String season) {
        CropManager.instances.put(location, season);
    }
    //清理无效的农作物并生长
    public static void CropGrow() {
        /*
        阶段1：更新数据
         */
        long start1 = System.currentTimeMillis();
        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        if (CropManager.instances != null) {
            //性能更高
            Set<Map.Entry<Location, String>> en = instances.entrySet();
            for(Map.Entry<Location, String> entry : en){
                Location key = entry.getKey();
                data.set(key.getWorld().getName() + "." + key.getBlockX() + "," + key.getBlockY()+ ","+ key.getBlockZ(), entry.getValue());
            }
        }
        long finish1 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f农作物数据更新耗时&a" + String.valueOf(finish1-start1) + "&fms",Bukkit.getConsoleSender());
        /*
        阶段2：清理数据内无效的农作物
         */
        long start2 = System.currentTimeMillis();

        List<Location> locations = new ArrayList<Location>();

        ConfigManager.Config.worlds.forEach(worldName ->{
            if(data.contains(worldName)){
                World world = Bukkit.getWorld(worldName);
                data.getConfigurationSection(worldName).getKeys(false).forEach(key ->{
                    String[] string_list = StringUtils.split(key,",");
                    if (world.isChunkLoaded(Integer.parseInt(string_list[0])/16, Integer.parseInt(string_list[2])/16)){
                        Location tempLoc = new Location(world,Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2]));
                        if(tempLoc.getBlock().getType() != Material.TRIPWIRE){
                            CropManager.instances.remove(tempLoc);
                            data.set(worldName+"."+string_list[0]+","+string_list[1]+","+string_list[2], null);
                        }else {
                            locations.add(new Location(world, Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2])));
                        }
                    }
                });
            }
        });
        long finish2 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f农作物缓存清理耗时&a" + String.valueOf(finish2-start2) + "&fms",Bukkit.getConsoleSender());
        /*
        阶段3：保存文件
         */
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("农作物缓存清理保存出错!");
        }
        /*
        阶段4：农作物生长判断
         */
        long start3 = System.currentTimeMillis();
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        ConfigManager.Config.worlds.forEach(worldName -> {

            World world = Bukkit.getWorld(worldName);
            locations.forEach(seedLocation -> {

                Location potLocation = seedLocation.clone().subtract(0,1,0);
                Block seedBlock = seedLocation.getBlock();
                Block potBlock = potLocation.getBlock();

                String[] seasons = StringUtils.split(data.getString(worldName + "." + seedLocation.getBlockX() + "," + seedLocation.getBlockY() + "," + seedLocation.getBlockZ()),",");

                if (CustomBlock.byAlreadyPlaced(potBlock) != null && CustomBlock.byAlreadyPlaced(seedBlock) != null){
                    String seedNamespace = CustomBlock.byAlreadyPlaced(seedBlock).getNamespacedID();
                    if (CustomBlock.byAlreadyPlaced(potBlock).getNamespacedID().equalsIgnoreCase(ConfigManager.Config.watered_pot) && seedNamespace.contains("stage")){
                        if (seedNamespace.equalsIgnoreCase(ConfigManager.Config.dead)){
                            return;
                        }

                        String[] split = StringUtils.split(seedNamespace,":");
                        String[] cropNameList = StringUtils.split(split[1],"_");

                        Label_out:
                        if(ConfigManager.Config.season){
                            if(ConfigManager.Config.greenhouse){
                                for(int i = 1; i <= ConfigManager.Config.range; i++){
                                    Location tempLocation = seedLocation.clone().add(0,i,0);
                                    if (CustomBlock.byAlreadyPlaced(tempLocation.getBlock()) != null){
                                        if(CustomBlock.byAlreadyPlaced(tempLocation.getBlock()).getNamespacedID().equalsIgnoreCase(ConfigManager.Config.glass)){
                                            break Label_out;
                                        }
                                    }
                                }
                            }
                            boolean wrongSeason = true;
                            for(String season : seasons){
                                if (Objects.equals(season, ConfigManager.Config.current)) {
                                    wrongSeason = false;
                                    break;
                                }
                            }
                            if(wrongSeason){
                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(ConfigManager.Config.dead, seedLocation);
                                    return null;
                                });
                                return;
                            }
                        }
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance( split[0] +":"+cropNameList[0] + "_stage_" + nextStage) != null) {
                            bukkitScheduler.callSyncMethod(CustomCrops.instance, () ->{
                                CustomBlock.remove(potLocation);
                                CustomBlock.place(ConfigManager.Config.pot, potLocation);
                                if(Math.random()< ConfigManager.Config.grow_chance){
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage,seedLocation);
                                }
                                return null;
                            });
                        }else if(ConfigManager.Config.big){
                            //农作物巨大化
                            if(config.getConfigurationSection("crops." + cropNameList[0]).getKeys(false).contains("gigantic")){
                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () ->{
                                    CustomBlock.remove(potLocation);
                                    CustomBlock.place(ConfigManager.Config.pot, potLocation);
                                    if(ConfigManager.Config.big_chance > Math.random()){
                                        CustomBlock.remove(seedLocation);
                                        CustomBlock.place(config.getString("crops." + cropNameList[0] + ".gigantic"),seedLocation);
                                    }
                                    return null;
                                });
                            }
                        }
                    }else if(CustomBlock.byAlreadyPlaced(potBlock).getNamespacedID().equalsIgnoreCase(ConfigManager.Config.pot) && seedNamespace.contains("stage")){
                        if (seedNamespace.equalsIgnoreCase(ConfigManager.Config.dead)){
                            return;
                        }
                        if(ConfigManager.Config.season) {
                            if(ConfigManager.Config.greenhouse){
                                for(int i = 1; i <= ConfigManager.Config.range; i++){
                                    Location tempLocation = seedLocation.clone().add(0,i,0);
                                    if (CustomBlock.byAlreadyPlaced(tempLocation.getBlock()) != null){
                                        if(CustomBlock.byAlreadyPlaced(tempLocation.getBlock()).getNamespacedID().equalsIgnoreCase(ConfigManager.Config.glass)){
                                            return;
                                        }
                                    }
                                }
                            }
                            boolean wrongSeason = true;
                            for (String season : seasons) {
                                if (Objects.equals(season, ConfigManager.Config.current)) {
                                    wrongSeason = false;
                                    break;
                                }
                            }
                            if (wrongSeason) {
                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () -> {
                                    CustomBlock.remove(seedLocation);
                                    CustomBlock.place(ConfigManager.Config.dead, seedLocation);
                                    return null;
                                });
                            }
                        }
                    }
                }
            });
        });
        long finish3 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f农作物生长判断耗时&a" + String.valueOf(finish3-start3) + "&fms",Bukkit.getConsoleSender());
    }
    /*
    //清理无效的农作物
    public static void cleanLoadedCache() {

        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);

        ConfigManager.Config.worlds.forEach(worldName ->{
            if(data.contains(worldName)){
                World world = Bukkit.getWorld(worldName);
                data.getConfigurationSection(worldName).getKeys(false).forEach(key ->{
                    String[] string_list = StringUtils.split(key,",");
                    if (world.isChunkLoaded(Integer.parseInt(string_list[0])/16, Integer.parseInt(string_list[2])/16)){
                        Location tempLoc = new Location(world,Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2]));
                        if(world.getBlockAt(tempLoc).getType() != Material.TRIPWIRE){
                            CropManager.instances.remove(tempLoc);
                            data.set(worldName+"."+string_list[0]+","+string_list[1]+","+string_list[2], null);
                        }
                    }
                });
            }
        });
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("农作物缓存清理保存出错!");
        }
    }
     */
}
