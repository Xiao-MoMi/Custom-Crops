package net.momirealms.customcrops.datamanager;

import net.momirealms.customcrops.utils.AdventureManager;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.fertilizer.RetainingSoil;
import net.momirealms.customcrops.fertilizer.SpeedGrow;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class PotManager {

    private CustomCrops plugin;
    public static ConcurrentHashMap<Location, Fertilizer> Cache = new ConcurrentHashMap<>();

    public PotManager(CustomCrops plugin){
        this.plugin = plugin;
    }

    public void loadData(){
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "pot.yml");
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomCrops] 种植盆数据文件生成失败!</red>");
            }
        }
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.getKeys(false).forEach(worldName -> {
            if (ConfigReader.Config.worldNames.contains(worldName)){
                data.getConfigurationSection(worldName).getValues(false).forEach((key, value) ->{
                    String[] split = StringUtils.split(key, ",");
                    if (value instanceof MemorySection map){
                        String name = (String) map.get("fertilizer");
                        Fertilizer fertilizer = ConfigReader.FERTILIZERS.get(name);
                        if (fertilizer == null) return;
                        if (fertilizer instanceof SpeedGrow speedGrow){
                            Cache.put(new Location(Bukkit.getWorld(worldName), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])), new SpeedGrow(name, (int) map.get("times"), speedGrow.getChance(), speedGrow.isBefore()));
                        }else if (fertilizer instanceof QualityCrop qualityCrop){
                            Cache.put(new Location(Bukkit.getWorld(worldName), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])), new QualityCrop(name, (int) map.get("times"), qualityCrop.getChance(), qualityCrop.isBefore()));
                        }else if (fertilizer instanceof RetainingSoil retainingSoil){
                            Cache.put(new Location(Bukkit.getWorld(worldName), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])), new RetainingSoil(name, (int) map.get("times"), retainingSoil.getChance(), retainingSoil.isBefore()));
                        }else {
                            AdventureManager.consoleMessage("<red>[CustomCrops] 未知肥料类型错误!</red>");
                        }
                    }
                });
            }
        });
    }

    public void saveData(){
        File file = new File(CustomCrops.instance.getDataFolder(), "data" + File.separator + "pot.yml");
        YamlConfiguration data = new YamlConfiguration();
        Cache.forEach(((location, fertilizer) -> {
            String world = location.getWorld().getName();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            data.set(world + "." + x + "," + y + "," + z + ".fertilizer", fertilizer.getKey());
            data.set(world + "." + x + "," + y + "," + z + ".times", fertilizer.getTimes());
        }));
        try {
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
