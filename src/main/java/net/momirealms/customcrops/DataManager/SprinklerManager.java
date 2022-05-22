package net.momirealms.customcrops.DataManager;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.IAFurniture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SprinklerManager {

    public static HashMap<Location, String> instances;

    //开服的时候将文件的数据读入
    public SprinklerManager(FileConfiguration data) {
        FileConfiguration config = CustomCrops.instance.getConfig();
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        data = YamlConfiguration.loadConfiguration(file);
        try {
            for (String world : config.getStringList("config.whitelist-worlds")) {
                SprinklerManager.instances = new HashMap<Location, String>();
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
            SprinklerManager.instances = new HashMap<Location, String>();
            e.printStackTrace();
        }
        saveData();
    }
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
            SprinklerManager.instances = new HashMap<Location, String>();
            Bukkit.getConsoleSender().sendMessage("错误:请联系开发者并提供报错信息");
        }
        try {
            data.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void putInstance(Location location, String type) {
        SprinklerManager.instances.put(location, type);
    }
    public static void cleanCache() {
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        //map不能一边循环一边删除
        //创建一个新的HashSet,用作循环
        Bukkit.getScheduler().callSyncMethod(CustomCrops.instance,()->{
            Set<Location> key = new HashSet(instances.keySet());
            try{
                for(Location u_key : key) {
                    if (!IAFurniture.getFromLocation(u_key.clone().add(0.5,0.5,0.5), u_key.getWorld())){
                        SprinklerManager.instances.remove(u_key);
                        data.set(u_key.getWorld().getName()+"."+u_key.getBlockX()+","+u_key.getBlockY()+","+u_key.getBlockZ(), null);
                    }
                }
            }catch (ConcurrentModificationException e){
                e.printStackTrace();
            }
            try{
                data.save(file);
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        });
    }
}
