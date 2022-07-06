package net.momirealms.customcrops;

import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.fertilizer.RetainingSoil;
import net.momirealms.customcrops.fertilizer.SpeedGrow;
import net.momirealms.customcrops.integrations.*;
import net.momirealms.customcrops.requirements.Biome;
import net.momirealms.customcrops.requirements.Permission;
import net.momirealms.customcrops.requirements.Requirement;
import net.momirealms.customcrops.requirements.YPos;
import net.momirealms.customcrops.utils.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigReader {

    public static HashMap<String, CropInstance> CROPS = new HashMap<>();
    public static HashMap<String, Fertilizer> FERTILIZERS = new HashMap<>();
    public static HashMap<String, WateringCan> CANS = new HashMap<>();
    public static HashMap<String, Sprinkler> SPRINKLERS = new HashMap<>();

    private static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomCrops.instance.getDataFolder(), configName);
        if (!file.exists()) {
            CustomCrops.instance.saveResource(configName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void ReloadConfig(){
        Config.loadConfig();
        Message.loadMessage();
        Basic.loadBasic();
        cropLoad();
        fertilizerLoad();
        Season.loadSeason();
    }

    public static class Config{

        public static List<World> worlds;
        public static List<String> worldNames;
        public static List<Long> cropGrowTimeList;
        public static List<Integration> integration;
        public static boolean asyncCheck;
//        public static boolean useBoneMeal;
//        public static boolean consumeWater;
//        public static double boneMealChance;
//        public static String success;
//        public static String failure;
        public static boolean enableLimit;
        public static int cropLimit;
        public static int sprinklerLimit;
        public static int yMin;
        public static int yMax;
        public static int sprinklerRefill;
        public static int timeToGrow;
        public static boolean logTime;
        public static boolean onlyLoadedGrow;
        public static boolean quality;
        public static double quality_1;
        public static double quality_2;

        public static void loadConfig(){

            //存读基本配置文件
            CustomCrops.instance.saveDefaultConfig();
            CustomCrops.instance.reloadConfig();
            FileConfiguration config = CustomCrops.instance.getConfig();

            //农作物生长时间点
            cropGrowTimeList = config.getLongList("config.grow-time");
            cropGrowTimeList.forEach(time -> {
                if(time < 0 || time > 23999){
                    AdventureManager.consoleMessage("<red>[CustomCrops] 农作物生长时间点必须位于0-23999之间");
                    cropGrowTimeList.remove(time);
                }
            });

            timeToGrow = config.getInt("config.time-to-grow",60)*20;

            //异步读取时间
            asyncCheck = config.getBoolean("config.async-time-check",false);
            logTime = config.getBoolean("config.log-time-consume",false);
            onlyLoadedGrow = !config.getBoolean("config.only-grow-in-loaded-chunks",true);

            //骨粉设置（已废弃）
//            useBoneMeal = config.getBoolean("config.bone-meal.enable",false);
//            if (useBoneMeal){
//                boneMealChance = config.getDouble("config.bone-meal.chance");
//                consumeWater = config.getBoolean("config.bone-meal.consume-water");
//                success = config.getString("config.bone-meal.particle.success");
//                failure = config.getString("config.bone-meal.particle.failure");
//            }

            //数量与高度限制
            enableLimit = config.getBoolean("config.limit.enable",true);
            if (enableLimit){
                cropLimit = config.getInt("config.limit.crop",64);
                sprinklerLimit = config.getInt("config.limit.sprinkler",16);
            }
            if (Bukkit.getServer().getClass().getPackage().getName().contains("16") || Bukkit.getServer().getClass().getPackage().getName().contains("17")){
                yMin = 0;
                yMax = 256;
            }
            if (Bukkit.getServer().getClass().getPackage().getName().contains("18") || Bukkit.getServer().getClass().getPackage().getName().contains("19")){
                yMin = -64;
                yMax = 320;
            }

            //农作物品质处理
            quality = config.getBoolean("config.quality.enable");
            if (quality){
                String[] split = StringUtils.split(config.getString("config.quality.default-ratio"), "/");
                double[] ratios = new double[3];
                ratios[0] = Double.parseDouble(split[0]);
                ratios[1] = Double.parseDouble(split[1]);
                ratios[2] = Double.parseDouble(split[2]);
                double total = ratios[0] + ratios[1] + ratios[2];
                quality_1 = ratios[0]/total;
                quality_2 = 1 - ratios[1]/total;
            }

            sprinklerRefill = config.getInt("config.sprinkler-refill",2);

            //农作物生长的白名单世界
            worlds = new ArrayList<>();
            worldNames = config.getStringList("config.whitelist-worlds");
            worldNames.forEach(worldName -> {
                World world = Bukkit.getWorld(worldName);
                if (world == null){
                    worldNames.remove(worldName);
                    AdventureManager.consoleMessage("<red>[CustomCrops] 世界" + worldName + "" + "不存在");
                }else {
                    worlds.add(world);
                }
            });

            //处理插件兼容性
            integration = new ArrayList<>();
            if(config.getBoolean("config.integration.Residence",false)){
                if(Bukkit.getPluginManager().getPlugin("Residence") == null){
                    CustomCrops.instance.getLogger().warning("未检测到插件 Residence!");
                }else {
                    integration.add(new Residence());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已启用 <gold>Residence <color:#FFEBCD>保护!");
                }
            }
            if(config.getBoolean("config.integration.Kingdoms",false)){
                if(Bukkit.getPluginManager().getPlugin("Kingdoms") == null){
                    CustomCrops.instance.getLogger().warning("未检测到插件 Kingdoms!");
                }else {
                    integration.add(new KingdomsX());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已启用 <gold>KingdomsX <color:#FFEBCD>保护!");
                }
            }
            if(config.getBoolean("config.integration.WorldGuard",false)){
                if(Bukkit.getPluginManager().getPlugin("WorldGuard") == null){
                    CustomCrops.instance.getLogger().warning("未检测到插件 WorldGuard!");
                }else {
                    integration.add(new WorldGuard());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已启用 <gold>WorldGuard <color:#FFEBCD>保护!");
                }
            }
            if(config.getBoolean("config.integration.GriefDefender",false)){
                if(Bukkit.getPluginManager().getPlugin("GriefDefender") == null){
                    CustomCrops.instance.getLogger().warning("未检测到插件 GriefDefender!");
                }else {
                    integration.add(new GriefDefender());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已启用 <gold>GriefDefender <color:#FFEBCD>保护!");
                }
            }
            if(config.getBoolean("config.integration.PlotSquared",false)){
                if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null){
                    CustomCrops.instance.getLogger().warning("未检测到插件 PlotSquared!");
                }else {
                    integration.add(new PlotSquared());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已启用 <gold>PlotSquared <color:#FFEBCD>保护!");
                }
            }
            if(config.getBoolean("config.integration.Towny",false)){
                if(Bukkit.getPluginManager().getPlugin("Towny") == null){
                    CustomCrops.instance.getLogger().warning("未检测到插件 Towny!");
                }else {
                    integration.add(new Towny());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已启用 <gold>Towny <color:#FFEBCD>保护!");
                }
            }
        }
    }

    public static class Basic{

        public static String pot;
        public static String watered_pot;
        public static String glass;
        public static String sprinkler_1;
        public static String sprinkler_2;
        public static String sprinkler_1i;
        public static String sprinkler_2i;
        public static String dead;
        public static String soilDetector;

        public static void loadBasic(){
            YamlConfiguration config = getConfig("basic.yml");
            pot = config.getString("basic.pot");
            watered_pot = config.getString("basic.watered-pot");
            glass = config.getString("basic.greenhouse-glass");
            sprinkler_1 = config.getString("basic.sprinkler-1");
            sprinkler_2 = config.getString("basic.sprinkler-2");
            sprinkler_1i = config.getString("basic.sprinkler-1-item");
            sprinkler_2i = config.getString("basic.sprinkler-2-item");
            dead = config.getString("basic.dead-crop");
            soilDetector = StringUtils.split(config.getString("basic.soil-detector"),":")[1];

            CANS.clear();
            if (config.contains("water-can")){
                config.getConfigurationSection("water-can").getKeys(false).forEach(key -> {
                    if (key.equals(StringUtils.split(config.getString("water-can." + key + ".item"),":")[1])){
                        int width = config.getInt("water-can." + key + ".width");
                        if (width % 2 == 0){
                            AdventureManager.consoleMessage("<red>[CustomCrops] 水壶 " + key + " 的浇灌宽度必须为奇数!</red>");
                            return;
                        }
                        WateringCan wateringCan = new WateringCan(config.getInt("water-can." + key + ".max"), width, config.getInt("water-can." + key + ".length"));
                        CANS.put(key, wateringCan);
                    }else {
                        AdventureManager.consoleMessage("<red>[CustomCrops] 水壶 " + key + " 与ItemsAdder物品ID不一致</red>");
                    }
                });
            }
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已载入 <white>" + CANS.size() + " <color:#FFEBCD>个水壶!");

            SPRINKLERS.clear();
            if (config.contains("sprinkler")){
                config.getConfigurationSection("sprinkler").getKeys(false).forEach(key -> {
                    if (key.equals(StringUtils.split(config.getString("sprinkler." + key + ".3Ditem"),":")[1])){
                        Sprinkler sprinklerData = new Sprinkler(config.getInt("sprinkler." + key + ".range"), config.getInt("sprinkler." + key + ".max-water"));
                        sprinklerData.setNamespacedID_2(config.getString("sprinkler." + key + ".3Ditem"));
                        String twoD = config.getString("sprinkler." + key + ".2Ditem");
                        sprinklerData.setNamespacedID_1(twoD);
                        SPRINKLERS.put(key, sprinklerData);
                        SPRINKLERS.put(StringUtils.split(twoD,":")[1], sprinklerData);
                    }else {
                        AdventureManager.consoleMessage("<red>[CustomCrops] 洒水器 " + key + " 与ItemsAdder物品ID不一致</red>");
                    }
                });
            }
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已载入 <white>" + SPRINKLERS.size()/2 + " <color:#FFEBCD>个洒水器!");
        }
    }

    public static class Season{

        public static boolean enable;
        public static boolean greenhouse;
        public static boolean seasonChange;
        public static int range;
        public static int duration;

        public static void loadSeason(){
            YamlConfiguration config = getConfig("season.yml");

            enable = config.getBoolean("season.enable",false);
            if (enable){
                greenhouse = config.getBoolean("season.greenhouse.enable",false);
                if (greenhouse) {
                    range = config.getInt("season.greenhouse.range",7);
                }
                seasonChange = config.getBoolean("season.auto-season-change.enable",false);
                duration = config.getInt("season.auto-season-change.duration",28);
                if (seasonChange) {
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>当前季节变换模式: <gold>自动");
                }else {
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>当前季节变换模式: <gold>指令");
                }
            }
        }
    }

    public static class Message{

        public static String prefix;
        public static String reload;
        public static String lackArgs;
        public static String noPerm;
        public static String spring;
        public static String summer;
        public static String autumn;
        public static String winter;
        public static String sprinkler_limit;
        public static String crop_limit;
        public static String not_configed;
        public static String badY;
        public static String badBiome;
        public static String badWorld;
        public static String badPerm;
        public static String badSeason;
        public static String forceGrow;
        public static String forceWater;
        public static String backUp;
        public static String setSeason;
        public static String wrongArgs;
        public static String forceSave;
        public static String noSeason;
        public static boolean hasCropInfo;
        public static boolean hasSprinklerInfo;
        public static boolean hasWaterInfo;
        public static int cropTime;
        public static int sprinklerTime;
        public static String cropText;
        public static String sprinklerLeft;
        public static String sprinklerFull;
        public static String sprinklerEmpty;
        public static String sprinklerRight;
        public static String beforePlant;
        public static String waterLeft;
        public static String waterFull;
        public static String waterEmpty;
        public static String waterRight;
        public static double cropOffset;
        public static double sprinklerOffset;

        public static void loadMessage(){
            YamlConfiguration config = getConfig("messages.yml");
            prefix = config.getString("messages.prefix");
            reload = config.getString("messages.reload");
            lackArgs = config.getString("messages.lack-args");
            noPerm = config.getString("messages.no-perm");
            spring = config.getString("messages.spring");
            summer = config.getString("messages.summer");
            autumn = config.getString("messages.autumn");
            winter = config.getString("messages.winter");
            sprinkler_limit = config.getString("messages.sprinkler-limit");
            crop_limit = config.getString("messages.crop-limit");
            not_configed = config.getString("messages.not-configed");
            badY = config.getString("messages.bad-Y");
            badBiome = config.getString("messages.bad-biome");
            badWorld = config.getString("messages.bad-world");
            badPerm = config.getString("messages.bad-perm");
            badSeason = config.getString("messages.bad-season");
            forceGrow = config.getString("messages.force-grow");
            forceWater = config.getString("messages.force-water");
            backUp = config.getString("messages.back-up");
            setSeason = config.getString("messages.set-season");
            wrongArgs = config.getString("messages.wrong-args");
            forceSave = config.getString("messages.force-save");
            beforePlant = config.getString("messages.before-plant");
            noSeason = config.getString("messages.no-season","当前世界没有季节");

            hasCropInfo = config.getBoolean("hologram.grow-info.enable");
            if (hasCropInfo){
                cropTime = config.getInt("hologram.grow-info.duration");
                cropText = config.getString("hologram.grow-info.text");
                cropOffset = config.getDouble("hologram.grow-info.y-offset");
            }
            hasSprinklerInfo = config.getBoolean("hologram.sprinkler-info.enable");
            if (hasSprinklerInfo){
                sprinklerTime = config.getInt("hologram.sprinkler-info.duration");
                sprinklerLeft = config.getString("hologram.sprinkler-info.left");
                sprinklerFull = config.getString("hologram.sprinkler-info.full");
                sprinklerEmpty = config.getString("hologram.sprinkler-info.empty");
                sprinklerRight = config.getString("hologram.sprinkler-info.right");
                sprinklerOffset = config.getDouble("hologram.sprinkler-info.y-offset");
            }
            hasWaterInfo = config.getBoolean("actionbar.watering-can.enable");
            if (hasWaterInfo){
                waterLeft = config.getString("actionbar.watering-can.left");
                waterFull = config.getString("actionbar.watering-can.full");
                waterEmpty = config.getString("actionbar.watering-can.empty");
                waterRight = config.getString("actionbar.watering-can.right");
            }
        }
    }

    public static void cropLoad(){
        CROPS.clear();
        YamlConfiguration config = getConfig("crops.yml");
        Set<String> keys = config.getConfigurationSection("crops").getKeys(false);
        keys.forEach(key -> {
            CropInstance cropInstance;
            if (config.contains("crops." + key + ".amount")){
                String[] split = StringUtils.split(config.getString("crops." + key + ".amount"),"~");
                cropInstance = new CropInstance(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
            }else {
                AdventureManager.consoleMessage("<red>[CustomCrops] 未设置农作物 " + key +" 的产物数量!</red>");
                return;
            }
            if (config.contains("crops." + key + ".gigantic")){
                cropInstance.setGiant(config.getString("crops." + key + ".gigantic.block"));
                cropInstance.setGiantChance(config.getDouble("crops." + key + ".gigantic.chance"));
            }
            if (Season.enable && config.contains("crops." + key + ".season")){
                cropInstance.setSeasons(config.getStringList("crops." + key + ".season"));
            }
            if (config.contains("crops." + key + ".return")){
                cropInstance.setReturnStage(config.getString("crops." + key + ".return"));
            }
            if (config.contains("crops." + key + ".requirements")){
                List<Requirement> requirements = new ArrayList<>();
                config.getConfigurationSection("crops." + key + ".requirements").getValues(false).forEach((requirement, value) -> {
                    switch (requirement){
                        case "world" -> requirements.add(new net.momirealms.customcrops.requirements.World((List<String>) value));
                        case "yPos" -> requirements.add(new YPos((List<String>) value));
                        case "biome" -> requirements.add(new Biome((List<String>) value));
                        case "permission" -> requirements.add(new Permission((String) value));
                    }
                });
                cropInstance.setRequirements(requirements);
            }
            if (Config.quality){
                cropInstance.setQuality_1(config.getString("crops." + key + ".quality.1"));
                cropInstance.setQuality_2(config.getString("crops." + key + ".quality.2"));
                cropInstance.setQuality_3(config.getString("crops." + key + ".quality.3"));
            }
            CROPS.put(key, cropInstance);
        });
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已载入 <white>" + CROPS.size() + " <color:#FFEBCD>种农作物!");
    }

    public static void fertilizerLoad(){
        FERTILIZERS.clear();
        YamlConfiguration config = getConfig("fertilizer.yml");
        if (config.contains("加速肥料")){
            config.getConfigurationSection("加速肥料").getKeys(false).forEach(key -> {
                if (StringUtils.split(config.getString("加速肥料." + key + ".item"), ":")[1].equals(key)){
                    SpeedGrow speedGrow = new SpeedGrow(key, config.getInt("加速肥料." + key + ".times"), config.getDouble("加速肥料." + key + ".chance"), config.getBoolean("加速肥料." + key + ".before-plant"));
                    speedGrow.setName(config.getString("加速肥料." + key + ".name"));
                    FERTILIZERS.put(key, speedGrow);
                }else {
                    AdventureManager.consoleMessage("<red>[CustomCrops] 肥料 " + key + " 与ItemsAdder物品ID不一致</red>");
                }
            });
        }
        if (config.contains("保湿肥料")){
            config.getConfigurationSection("保湿肥料").getKeys(false).forEach(key -> {
                if (StringUtils.split(config.getString("保湿肥料." + key + ".item"), ":")[1].equals(key)){
                    RetainingSoil retainingSoil = new RetainingSoil(key, config.getInt("保湿肥料." + key + ".times"), config.getDouble("保湿肥料." + key + ".chance"), config.getBoolean("保湿肥料." + key + ".before-plant"));
                    retainingSoil.setName(config.getString("保湿肥料." + key + ".name"));
                    FERTILIZERS.put(key, retainingSoil);
                }else {
                    AdventureManager.consoleMessage("<red>[CustomCrops] 肥料 " + key + " 与ItemsAdder物品ID不一致</red>");
                }
            });
        }
        if (config.contains("品质肥料")){
            config.getConfigurationSection("品质肥料").getKeys(false).forEach(key -> {
                if (StringUtils.split(config.getString("品质肥料." + key + ".item"), ":")[1].equals(key)){
                    String[] split = StringUtils.split(config.getString("品质肥料." + key + ".chance"), "/");
                    int[] weight = new int[3];
                    weight[0] = Integer.parseInt(split[0]);
                    weight[1] = Integer.parseInt(split[1]);
                    weight[2] = Integer.parseInt(split[2]);
                    QualityCrop qualityCrop = new QualityCrop(key, config.getInt("品质肥料." + key + ".times"), weight, config.getBoolean("品质肥料." + key + ".before-plant"));
                    qualityCrop.setName(config.getString("品质肥料." + key + ".name"));
                    FERTILIZERS.put(key, qualityCrop);
                }else {
                    AdventureManager.consoleMessage("<red>[CustomCrops] 肥料 " + key + " 与ItemsAdder物品ID不一致</red>");
                }
            });
        }
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>已载入 <white>" + FERTILIZERS.size() + " <color:#FFEBCD>种肥料!");
    }
}
