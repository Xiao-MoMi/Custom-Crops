package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.ConfigManager;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.IAFurniture;
import net.momirealms.customcrops.MessageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SprinklerManager {

    public static ConcurrentHashMap<Location, String> instances;

    //10W性能测试
    public static void testData_3(){
        for(int i = -50000; i < 50000; i++){
            Location tempLoc = new Location(Bukkit.getWorld("world"),i,100,i);
            String name = "s1";
            instances.put(tempLoc, name);
        }
    }

    //开服的时候将文件的数据读入
    public SprinklerManager(FileConfiguration data) {

        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        data = YamlConfiguration.loadConfiguration(file);

        try {
            for (String world : config.getStringList("config.whitelist-worlds")) {
                SprinklerManager.instances = new ConcurrentHashMap<Location, String>();
                if(data.getConfigurationSection(world) != null){
                    for (String coordinate : data.getConfigurationSection(world).getKeys(false)) {
                        Location tempLocation = new Location(Bukkit.getWorld(world), Integer.parseInt(coordinate.split(",")[0]), Integer.parseInt(coordinate.split(",")[1]), Integer.parseInt(coordinate.split(",")[2]));
                        String type = data.getString(world + "." + coordinate);
                        SprinklerManager.instances.put(tempLocation, type);
                    }
                }
            }
        }
        catch (Exception e) {
            SprinklerManager.instances = new ConcurrentHashMap<Location, String>();
            e.printStackTrace();
        }
        saveData();
    }
    /*
    //根据世界名获取所有的洒水器
    public static List<Location> getSprinklers(World world){
        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        List<Location> locations = new ArrayList<Location>();
        if (config.getStringList("config.whitelist-worlds").contains(world.getName())){
            if(data.contains(world.getName())){
                data.getConfigurationSection(world.getName()).getKeys(false).forEach(key ->{
                    String[] string_list = key.split(",");
                    //只返回被加载的区块中的洒水器坐标
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
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        if (SprinklerManager.instances != null) {
            //性能更高
            Set<Map.Entry<Location, String>> en = instances.entrySet();
            for(Map.Entry<Location, String> entry : en){
                data.set(entry.getKey().getWorld().getName() + "." + entry.getKey().getBlockX() + "," + entry.getKey().getBlockY()+ ","+entry.getKey().getBlockZ(), entry.getValue());
            }
        }
        else {
            SprinklerManager.instances = new ConcurrentHashMap<Location, String>();
            Bukkit.getConsoleSender().sendMessage("错误：空数据");
        }
        try {
            data.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("洒水器数据保存出错");
        }
    }
    public static void putInstance(Location location, String type) {
        SprinklerManager.instances.put(location, type);
    }
    public static void SprinklerWork() {
         /*
        阶段1：更新数据
         */
        long start1 = System.currentTimeMillis();
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        if (SprinklerManager.instances != null) {
            //性能更高
            Set<Map.Entry<Location, String>> en = instances.entrySet();
            for(Map.Entry<Location, String> entry : en){
                data.set(entry.getKey().getWorld().getName() + "." + entry.getKey().getBlockX() + "," + entry.getKey().getBlockY()+ ","+entry.getKey().getBlockZ(), entry.getValue());
            }
        }
        long finish1 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f洒水器数据更新耗时&a" + String.valueOf(finish1-start1) + "&fms",Bukkit.getConsoleSender());
        /*
        阶段2：清理数据内无效的农作物
         */
        Bukkit.getScheduler().callSyncMethod(CustomCrops.instance,()->{
            long start2 = System.currentTimeMillis();
            //map不能一边循环一边删除
            //创建一个新的HashSet,用作循环
            //检测碰撞体积需要同步
            List<Location> locations = new ArrayList<Location>();

            ConfigManager.Config.worlds.forEach(worldName ->{
                if(data.contains(worldName)){
                    World world = Bukkit.getWorld(worldName);
                    data.getConfigurationSection(worldName).getKeys(false).forEach(key ->{
                        String[] string_list = StringUtils.split(key,",");
                        if (world.isChunkLoaded(Integer.parseInt(string_list[0])/16, Integer.parseInt(string_list[2])/16)){
                            Location tempLoc = new Location(world,Double.parseDouble(string_list[0])+0.5,Double.parseDouble(string_list[1])+0.5,Double.parseDouble(string_list[2])+0.5);
                            if(!IAFurniture.getFromLocation(tempLoc, world)){
                                SprinklerManager.instances.remove(tempLoc);
                                data.set(worldName+"."+string_list[0]+","+string_list[1]+","+string_list[2], null);
                            }else {
                                locations.add(new Location(world, Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2])));
                            }
                        }
                    });
                }
            });
            /*
            Set<Location> key = new HashSet(instances.keySet());
            for(Location u_key : key) {
                if (!IAFurniture.getFromLocation(u_key.clone().add(0.5,0.5,0.5), u_key.getWorld())){
                    SprinklerManager.instances.remove(u_key);
                    data.set(u_key.getWorld().getName()+"."+u_key.getBlockX()+","+u_key.getBlockY()+","+u_key.getBlockZ(), null);
                }else {
                    locations.add(new Location(world, Double.parseDouble(string_list[0]),Double.parseDouble(string_list[1]),Double.parseDouble(string_list[2])));
                }
            }
            */
            long finish2 = System.currentTimeMillis();
            MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f洒水器缓存清理耗时&a" + String.valueOf(finish2-start2) + "&fms",Bukkit.getConsoleSender());

            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.instance,()->{
                /*
                阶段3：保存数据
                */
                try{
                    data.save(file);
                }catch (IOException e){
                    e.printStackTrace();
                    CustomCrops.instance.getLogger().warning("洒水器缓存清理保存出错!");
                }
                /*
                阶段4：洒水器工作
                */
                long start3 = System.currentTimeMillis();
                ConfigManager.Config.worlds.forEach(worldName -> {

                    World world = Bukkit.getWorld(worldName);
                    locations.forEach(location -> {

                        String type = data.getString(worldName + "." + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
                        if(type.equals("s1")){
                            for(int i = -1; i <= 1;i++){
                                for (int j = -1; j <= 1; j++){
                                    Location tempLoc = location.clone().add(i,-1,j);
                                    waterPot(tempLoc, world);
                                }
                            }
                        }else{
                            for(int i = -2; i <= 2;i++){
                                for (int j = -2; j <= 2; j++){
                                    Location tempLoc = location.clone().add(i,-1,j);
                                    waterPot(tempLoc, world);
                                }
                            }
                        }
                    });
                });
                long finish3 = System.currentTimeMillis();
                MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops|性能监测] &f洒水器工作耗时&a" + String.valueOf(finish3-start3) + "&fms",Bukkit.getConsoleSender());
            });
            return null;
        });
    }
    private static void waterPot(Location tempLoc, World world) {
        if(CustomBlock.byAlreadyPlaced(tempLoc.getBlock()) != null){
            if(CustomBlock.byAlreadyPlaced(tempLoc.getBlock()).getNamespacedID().equalsIgnoreCase(ConfigManager.Config.pot)){
                Bukkit.getScheduler().callSyncMethod(CustomCrops.instance,()->{
                    CustomBlock.remove(tempLoc);
                    CustomBlock.place((ConfigManager.Config.watered_pot), tempLoc);
                    return null;
                });
            }
        }
    }
}
