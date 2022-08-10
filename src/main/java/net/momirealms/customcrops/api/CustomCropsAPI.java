package net.momirealms.customcrops.api;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.datamanager.SeasonManager;

public class CustomCropsAPI {

    /**
     * 获取插件实例
     * @return 插件实例
     */
    public static CustomCrops getPlugin(){
        return CustomCrops.plugin;
    }

    /**
     * 获取指定世界的季节
     * @param worldName 世界名
     * @return 那个世界的季节，若不存在则返回null
     */
    public static String getSeason(String worldName){
        return SeasonManager.SEASON.get(worldName);
    }
}
