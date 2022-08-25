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

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.objects.fertilizer.*;
import net.momirealms.customcrops.utils.AdventureManager;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.objects.SimpleLocation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class PotManager {

    public static ConcurrentHashMap<SimpleLocation, Fertilizer> Cache = new ConcurrentHashMap<>();

    /**
     * 载入数据
     */
    public void loadData(){
        File file = new File(CustomCrops.plugin.getDataFolder(), "data" + File.separator + "pot.yml");
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
            data.getConfigurationSection(worldName).getValues(false).forEach((keys, value) ->{
                String[] split = StringUtils.split(keys, ",");
                if (value instanceof MemorySection map){
                    String key = map.getString("fertilizer");
                    Fertilizer fertilizer = ConfigReader.FERTILIZERS.get(key);
                    if (fertilizer == null) return;
                    if (fertilizer instanceof SpeedGrow){
                        SpeedGrow speedGrow = new SpeedGrow(key, map.getInt("times"));
                        Cache.put(new SimpleLocation(worldName, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])), speedGrow);
                    }else if (fertilizer instanceof QualityCrop){
                        QualityCrop qualityCrop = new QualityCrop(key, map.getInt("times"));
                        Cache.put(new SimpleLocation(worldName, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])), qualityCrop);
                    }else if (fertilizer instanceof RetainingSoil){
                        RetainingSoil retainingSoil = new RetainingSoil(key, map.getInt("times"));
                        Cache.put(new SimpleLocation(worldName, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])), retainingSoil);
                    }else if(fertilizer instanceof YieldIncreasing){
                        YieldIncreasing yieldIncreasing = new YieldIncreasing(key, map.getInt("times"));
                        Cache.put(new SimpleLocation(worldName, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])), yieldIncreasing);
                    }else {
                        AdventureManager.consoleMessage("<red>[CustomCrops] 未知肥料类型错误!</red>");
                    }
                }
            });
        });
    }

    /**
     * 保存数据
     */
    public void saveData(){
        File file = new File(CustomCrops.plugin.getDataFolder(), "data" + File.separator + "pot.yml");
        YamlConfiguration data = new YamlConfiguration();
        Cache.forEach(((location, fertilizer) -> {
            String world = location.getWorldName();
            int x = location.getX();
            int y = location.getY();
            int z = location.getZ();
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
