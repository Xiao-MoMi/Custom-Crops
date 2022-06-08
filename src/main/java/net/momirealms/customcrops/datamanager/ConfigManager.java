package net.momirealms.customcrops.datamanager;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.utils.Crop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    public static HashMap<String, Crop> CONFIG;
    static {
        CONFIG = new HashMap<>();
    }

    public static class Config{

        public static boolean res;
        public static boolean wg;
        public static boolean king;
        public static boolean season;
        public static boolean need_water;
        public static boolean greenhouse;
        public static boolean limit;

        public static List<String> worlds;
        public static List<Long> cropGrowTimeList;
        public static List<Long> sprinklerWorkTimeList;

        public static String current;
        public static String prefix;
        public static String bad_place;
        public static String reload;
        public static String force_save;
        public static String nextSeason;
        public static String no_such_seed;
        public static String wrong_season;
        public static String season_set;
        public static String season_disabled;
        public static String force_grow;
        public static String force_water;
        public static String limit_crop;
        public static String limit_sprinkler;
        public static String backup;
        public static String spring;
        public static String summer;
        public static String autumn;
        public static String winter;
        public static String can_full;
        public static String pot;
        public static String watered_pot;
        public static String watering_can_1;
        public static String watering_can_2;
        public static String watering_can_3;
        public static String glass;
        public static String sprinkler_1;
        public static String sprinkler_2;
        public static String sprinkler_1i;
        public static String sprinkler_2i;
        public static String dead;
        public static String success;
        public static String failure;

        public static double bone_chance;

        public static int range;
        public static int maxh;
        public static int minh;
        public static int max_crop;
        public static int max_sprinkler;

        public static void ReloadConfig(){

            CustomCrops.instance.reloadConfig();
            FileConfiguration configuration = CustomCrops.instance.getConfig();
            cropLoad();

            //处理配置
            Config.res = configuration.getBoolean("integration.residence");
            Config.king = configuration.getBoolean("integration.kingdomsX");
            Config.wg = configuration.getBoolean("integration.worldguard");
            Config.season = configuration.getBoolean("enable-season");
            Config.need_water = configuration.getBoolean("config.bone-meal-consume-water");
            Config.greenhouse = configuration.getBoolean("config.enable-greenhouse");
            Config.limit = configuration.getBoolean("config.enable-limit");

            Config.bone_chance = configuration.getDouble("config.bone-meal-chance");

            Config.range = configuration.getInt("config.greenhouse-range");
            Config.maxh = configuration.getInt("config.height.max");
            Config.minh = configuration.getInt("config.height.min");
            Config.max_crop = configuration.getInt("config.max-crops");
            Config.max_sprinkler = configuration.getInt("config.max-sprinklers");

            Config.current = configuration.getString("current-season");
            Config.pot = configuration.getString("config.pot");
            Config.watered_pot = configuration.getString("config.watered-pot");
            Config.watering_can_1 = configuration.getString("config.watering-can-1");
            Config.watering_can_2 = configuration.getString("config.watering-can-2");
            Config.watering_can_3 = configuration.getString("config.watering-can-3");
            Config.glass = configuration.getString("config.greenhouse-glass");
            Config.sprinkler_1 = configuration.getString("config.sprinkler-1");
            Config.sprinkler_2 = configuration.getString("config.sprinkler-2");
            Config.sprinkler_1i = configuration.getString("config.sprinkler-1-item");
            Config.sprinkler_2i = configuration.getString("config.sprinkler-2-item");
            Config.dead = configuration.getString("config.dead-crop");
            Config.success = configuration.getString("config.particle.success");
            Config.failure = configuration.getString("config.particle.failure");

            Config.worlds = configuration.getStringList("config.whitelist-worlds");
            Config.cropGrowTimeList = configuration.getLongList("config.grow-time");
            Config.sprinklerWorkTimeList = configuration.getLongList("config.sprinkler-time");

            //处理消息
            Config.prefix = configuration.getString("messages.prefix");
            Config.bad_place = configuration.getString("messages.not-a-good-place");
            Config.reload = configuration.getString("messages.reload");
            Config.force_save = configuration.getString("messages.force-save");
            Config.nextSeason = configuration.getString("messages.nextseason");
            Config.no_such_seed = configuration.getString("messages.no-such-seed");
            Config.wrong_season = configuration.getString("messages.wrong-season");
            Config.season_set = configuration.getString("messages.season-set");
            Config.season_disabled = configuration.getString("messages.season-disabled");
            Config.force_grow = configuration.getString("messages.force-grow");
            Config.force_water = configuration.getString("messages.force-water");
            Config.limit_crop = configuration.getString("messages.reach-limit-crop");
            Config.limit_sprinkler = configuration.getString("messages.reach-limit-sprinkler");
            Config.can_full = configuration.getString("messages.can-full");
            Config.backup = configuration.getString("messages.backup");
            Config.spring = configuration.getString("messages.spring");
            Config.summer = configuration.getString("messages.summer");
            Config.autumn = configuration.getString("messages.autumn");
            Config.winter = configuration.getString("messages.winter");
            Config.winter = configuration.getString("messages.noperm");
        }

        /*
        根据文件名获取配置文件
        */
        public static YamlConfiguration getConfig(String configName) {

            File file = new File(CustomCrops.instance.getDataFolder(), configName);
            //文件不存在则生成默认配置
            if (!file.exists()) {
                CustomCrops.instance.saveResource(configName, false);
            }
            return YamlConfiguration.loadConfiguration(file);
        }

        /*
        加载农作物数据
        */
        public static void cropLoad(){
            try {
                CONFIG.clear();
                YamlConfiguration cropConfig = getConfig("crops.yml");
                Set<String> keys = cropConfig.getConfigurationSection("crops").getKeys(false);
                keys.forEach(key -> {
                    double chance = cropConfig.getDouble("crops."+key+".grow-chance");
                    Crop crop = new Crop(key, chance);
                    if(cropConfig.getConfigurationSection("crops."+key).contains("return")){
                        crop.setWillReturn(true);
                        crop.setReturnStage(cropConfig.getString("crops."+key+".return"));
                    }else {
                        crop.setWillReturn(false);
                    }
                    if(cropConfig.getConfigurationSection("crops."+key).contains("season")){
                        crop.setSeasons(StringUtils.split( cropConfig.getString("crops."+key+".season"), ","));
                    }
                    if(cropConfig.getConfigurationSection("crops."+key).contains("gigantic")){
                        crop.setWillGiant(true);
                        crop.setGiant(cropConfig.getString("crops."+key+".gigantic"));
                        crop.setGiantChance(cropConfig.getDouble("crops."+key+".gigantic-chance"));
                    }else {
                        crop.setWillGiant(false);
                    }
                    CONFIG.put(key, crop);
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                CustomCrops.instance.getLogger().warning("crops.yml加载失败!");
            }
        }
    }
}
