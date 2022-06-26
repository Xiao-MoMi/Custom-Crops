package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.utils.IAFurniture;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SprinklerManager {

    static ConcurrentHashMap<Location, String> SPRINKLERS;
    /*
    开服的时候将文件的数据读入
    */
    public static void loadData(){

        SPRINKLERS = new ConcurrentHashMap<>();

        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        for (String world : ConfigManager.Config.worlds) {
            if(data.getConfigurationSection(world) != null){
                for (String coordinate : data.getConfigurationSection(world).getKeys(false)) {
                    Location tempLocation = new Location(Bukkit.getWorld(world), Integer.parseInt(coordinate.split(",")[0]), Integer.parseInt(coordinate.split(",")[1]), Integer.parseInt(coordinate.split(",")[2]));
                    String type = data.getString(world + "." + coordinate);
                    SPRINKLERS.put(tempLocation, type);
                }
            }
        }
    }
    /*
    保存数据
    */
    public static void saveData(){

        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        Set<Map.Entry<Location, String>> en = SPRINKLERS.entrySet();
        for(Map.Entry<Location, String> entry : en){
            data.set(entry.getKey().getWorld().getName() + "." + entry.getKey().getBlockX() + "," + entry.getKey().getBlockY()+ ","+entry.getKey().getBlockZ(), entry.getValue());
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
        SPRINKLERS.put(location, type);
    }

    public static void SprinklerWork(String worldName) {
         /*
        阶段1：更新数据
         */
        long start1 = System.currentTimeMillis();
        File file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

        Set<Map.Entry<Location, String>> en = SPRINKLERS.entrySet();
        for(Map.Entry<Location, String> entry : en){
            data.set(entry.getKey().getWorld().getName() + "." + entry.getKey().getBlockX() + "," + entry.getKey().getBlockY()+ ","+entry.getKey().getBlockZ(), entry.getValue());
        }
        long finish1 = System.currentTimeMillis();
        if (ConfigManager.Config.log_time){
            MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] &7洒水器数据更新耗时&a" + (finish1-start1) + "&fms",Bukkit.getConsoleSender());
        }
        /*
        阶段2：清理数据内无效的洒水器并工作
        */
        bukkitScheduler.callSyncMethod(CustomCrops.instance,()->{
            long start2 = System.currentTimeMillis();
            //检测碰撞体积需要同步
            if(data.contains(worldName)){
                World world = Bukkit.getWorld(worldName);
                data.getConfigurationSection(worldName).getKeys(false).forEach(key ->{
                    String[] coordinate = StringUtils.split(key,",");
                    if (world.isChunkLoaded(Integer.parseInt(coordinate[0])/16, Integer.parseInt(coordinate[2])/16)){
                        Location tempLoc = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                        if(!IAFurniture.getFromLocation(tempLoc, world)){
                            SPRINKLERS.remove(tempLoc);
                            data.set(worldName+"."+coordinate[0]+","+coordinate[1]+","+coordinate[2], null);
                        }else {
                            String type = data.getString(worldName + "." + coordinate[0] + "," + coordinate[1] + "," + coordinate[2]);
                            if(type == null){
                                MessageManager.consoleMessage("错误数据位于"+ worldName + coordinate[0] + "," + coordinate[1] + "," + coordinate[2], Bukkit.getConsoleSender());
                                return;
                            }
                            if(type.equalsIgnoreCase("s1")){
                                for(int i = -1; i <= 1;i++){
                                    for (int j = -1; j <= 1; j++){
                                        waterPot(tempLoc.clone().add(i,-1,j));
                                    }
                                }
                            }else{
                                for(int i = -2; i <= 2;i++){
                                    for (int j = -2; j <= 2; j++){
                                        waterPot(tempLoc.clone().add(i,-1,j));
                                    }
                                }
                            }
                        }
                    }
                });
            }
            long finish2 = System.currentTimeMillis();
            if (ConfigManager.Config.log_time){
                MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] &7洒水器工作耗时&a" + (finish2-start2) + "&fms",Bukkit.getConsoleSender());
            }
            bukkitScheduler.runTaskAsynchronously(CustomCrops.instance,()->{
                /*
                阶段3：保存数据
                */
                long start3 = System.currentTimeMillis();
                try{
                    data.save(file);
                }catch (IOException e){
                    e.printStackTrace();
                    CustomCrops.instance.getLogger().warning("sprinkler-data.yml保存出错!");
                }
                long finish3 = System.currentTimeMillis();
                if (ConfigManager.Config.log_time){
                    MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] &7洒水器数据保存耗时&a" + (finish3-start3) + "&fms",Bukkit.getConsoleSender());
                }
            });
            return null;
        });
    }
    private static void waterPot(Location tempLoc) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
        if(cb != null){
            if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.pot)){
                CustomBlock.remove(tempLoc);
                CustomBlock.place((ConfigManager.Config.watered_pot), tempLoc);
            }
        }
    }
}
