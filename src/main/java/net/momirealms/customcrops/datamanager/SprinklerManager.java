package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.utils.AdventureManager;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.utils.IAFurniture;
import net.momirealms.customcrops.utils.Sprinkler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SprinklerManager {

    public YamlConfiguration data;
    private final CustomCrops plugin;
    public static ConcurrentHashMap<Location, Sprinkler> Cache = new ConcurrentHashMap<>();

    public SprinklerManager(CustomCrops plugin){
        this.plugin = plugin;
    }

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

    public void saveData(){
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "sprinkler.yml");
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomCrops] sprinkler.yml保存出错!</red>");
        }
    }

    public void cleanData(){
        data.getKeys(false).forEach(world -> {
            data.getConfigurationSection(world).getKeys(false).forEach(chunk ->{
                if (data.getConfigurationSection(world + "." + chunk).getKeys(false).size() == 0){
                    data.set(world + "." + chunk, null);
                }
            });
        });
    }

    public void updateData(){
        Cache.forEach((location, sprinklerData) -> {
            String world = location.getWorld().getName();
            int x = location.getBlockX();
            int z = location.getBlockZ();
            StringBuilder stringBuilder = new StringBuilder().append(world).append(".").append(x/16).append(",").append(z/16).append(".").append(x).append(",").append(location.getBlockY()).append(",").append(z);
            data.set(stringBuilder+".range", sprinklerData.getRange());
            data.set(stringBuilder+".water", sprinklerData.getWater());
        });
        Cache.clear();
    }

    public void sprinklerWork(String worldName){
        Long time1 = System.currentTimeMillis();
        updateData();
        Long time2 = System.currentTimeMillis();
        if (ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 洒水器数据更新" + (time2-time1) + "ms");
        }
        if (data.contains(worldName)){
            data.getConfigurationSection(worldName).getKeys(false).forEach(chunk ->{
                String[] split = StringUtils.split(chunk,",");
                World world = Bukkit.getWorld(worldName);
                if (ConfigReader.Config.onlyLoadedGrow || world.isChunkLoaded(Integer.parseInt(split[0]), Integer.parseInt(split[1]))) {
                    data.getConfigurationSection(worldName + "." + chunk).getValues(false).forEach((key, value) -> {
                        String[] coordinate = StringUtils.split(key, ",");
                        Location location = new Location(world,Double.parseDouble(coordinate[0])+0.5,Double.parseDouble(coordinate[1])+0.5,Double.parseDouble(coordinate[2])+0.5);
                        if (value instanceof MemorySection map){
                            Bukkit.getScheduler().callSyncMethod(CustomCrops.instance, ()->{
                                int water = (int) map.get("water");
                                int range = (int) map.get("range");
                                if(!IAFurniture.getFromLocation(location, world)){
                                    data.set(worldName + "." + chunk + "." + key, null);
                                    return null;
                                }
                                if (range == 0) data.set(worldName + "." + chunk + "." + key, null);
                                if (water > 0){
                                    data.set(worldName + "." + chunk + "." + key + ".water", water - 1);
                                    for(int i = -range; i <= range; i++){
                                        for (int j = -range; j <= range; j++){
                                            waterPot(location.clone().add(i,-1,j));
                                        }
                                    }
                                }
                                return null;
                            });
                        }
                    });
                }
            });
        }
        Long time3 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 洒水器工作过程" + (time3-time2) + "ms");
        }
        saveData();
        Long time4 = System.currentTimeMillis();
        if(ConfigReader.Config.logTime){
            AdventureManager.consoleMessage("性能监测: 洒水器数据保存" + (time4-time3) + "ms");
        }
    }

    private void waterPot(Location potLoc) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(potLoc.getBlock());
        if(cb != null){
            if(cb.getNamespacedID().equals(ConfigReader.Basic.pot)){
                CustomBlock.remove(potLoc);
                CustomBlock.place(ConfigReader.Basic.watered_pot, potLoc);
            }
        }
    }
}
