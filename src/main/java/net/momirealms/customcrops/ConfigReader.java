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

package net.momirealms.customcrops;

import net.kyori.adventure.key.Key;
import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.fertilizer.RetainingSoil;
import net.momirealms.customcrops.fertilizer.SpeedGrow;
import net.momirealms.customcrops.integrations.protection.*;
import net.momirealms.customcrops.integrations.skill.Aurelium;
import net.momirealms.customcrops.integrations.skill.MMOCore;
import net.momirealms.customcrops.integrations.skill.SkillXP;
import net.momirealms.customcrops.integrations.skill.mcMMO;
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
import java.util.*;

public class ConfigReader {

    public static HashMap<String, CropInstance> CROPS = new HashMap<>();
    public static HashMap<String, Fertilizer> FERTILIZERS = new HashMap<>();
    public static HashMap<String, WateringCan> CANS = new HashMap<>();
    public static HashMap<String, Sprinkler> SPRINKLERS = new HashMap<>();

    public static YamlConfiguration getConfig(String configName) {
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
        Sounds.loadSound();
    }

    public static class Config{

        public static List<World> worlds;
        public static List<String> worldNames;
        public static List<Long> cropGrowTimeList;
        public static List<Integration> integration;
        public static String referenceWorld;
        public static String lang;
        public static boolean asyncCheck;
        public static boolean enableLimit;
        public static boolean hasParticle;
        public static boolean rightClickHarvest;
        public static int cropLimit;
        public static int sprinklerLimit;
        public static int yMin;
        public static int yMax;
        public static int sprinklerRefill;
        public static int waterCanRefill;
        public static int timeToGrow;
        public static int timeToWork;
        public static boolean logTime;
        public static boolean onlyLoadedGrow;
        public static boolean quality;
        public static boolean canAddWater;
        public static boolean allWorld;
        public static boolean needEmptyHand;
        public static double quality_1;
        public static double quality_2;
        public static SkillXP skillXP;

        public static void loadConfig(){

            //存读基本配置文件
            CustomCrops.instance.saveDefaultConfig();
            CustomCrops.instance.reloadConfig();
            FileConfiguration config = CustomCrops.instance.getConfig();

            lang = config.getString("config.lang","chinese");

            //农作物生长时间点
            cropGrowTimeList = config.getLongList("config.grow-time");
            cropGrowTimeList.forEach(time -> {
                if(time < 0 || time > 23999){
                    AdventureManager.consoleMessage("<red>[CustomCrops] time should be between 0 and 23999");
                }
            });

            timeToGrow = config.getInt("config.time-to-grow",60)*20;
            timeToWork = config.getInt("config.time-to-work",30)*20;

            asyncCheck = config.getBoolean("config.async-time-check",false);
            logTime = config.getBoolean("config.log-time-consume",false);
            onlyLoadedGrow = !config.getBoolean("config.only-grow-in-loaded-chunks",true);
            allWorld = config.getBoolean("config.all-world-grow",false);
            hasParticle = config.getBoolean("config.water-particles", true);
            rightClickHarvest = config.getBoolean("config.right-click-harvest", true);
            needEmptyHand = config.getBoolean("config.harvest-with-empty-hand", true);

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

            //农作物品质
            quality = config.getBoolean("config.quality.enable",true);
            if (quality){
                String[] split = StringUtils.split(config.getString("config.quality.default-ratio","17/2/1"), "/");
                double[] ratios = new double[3];
                ratios[0] = Double.parseDouble(split[0]);
                ratios[1] = Double.parseDouble(split[1]);
                ratios[2] = Double.parseDouble(split[2]);
                double total = ratios[0] + ratios[1] + ratios[2];
                quality_1 = ratios[0]/total;
                quality_2 = 1 - ratios[1]/total;
            }

            sprinklerRefill = config.getInt("config.sprinkler-refill",2);
            waterCanRefill = config.getInt("config.water-can-refill",1);
            canAddWater = config.getBoolean("config.water-can-add-water-to-sprinkler",true);

            if (allWorld){
                if (config.getStringList("config.whitelist-worlds").size() > 1){
                    referenceWorld = config.getStringList("config.whitelist-worlds").get(0);
                    AdventureManager.consoleMessage("<red>[CustomCrops] Only one whitelist world is allowed when \"all-world-grow\" enabled!");
                }else {
                    referenceWorld = config.getStringList("config.whitelist-worlds").get(0);
                }
            }

            //农作物生长的白名单世界
            worlds = new ArrayList<>();
            worldNames = config.getStringList("config.whitelist-worlds");
            worldNames.forEach(worldName -> {
                World world = Bukkit.getWorld(worldName);
                if (world == null){
                    AdventureManager.consoleMessage("<red>[CustomCrops] World " + worldName + " doesn't exist");
                }else {
                    worlds.add(world);
                }
            });
            //处理插件兼容性
            integration = new ArrayList<>();
            if(config.getBoolean("config.integration.Residence",false)){
                if(Bukkit.getPluginManager().getPlugin("Residence") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize Residence!");
                }else {
                    integration.add(new Residence());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>Residence <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.Kingdoms",false)){
                if(Bukkit.getPluginManager().getPlugin("Kingdoms") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize Kingdoms!");
                }else {
                    integration.add(new KingdomsX());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>KingdomsX <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.WorldGuard",false)){
                if(Bukkit.getPluginManager().getPlugin("WorldGuard") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize WorldGuard!");
                }else {
                    integration.add(new WorldGuard());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>WorldGuard <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.GriefDefender",false)){
                if(Bukkit.getPluginManager().getPlugin("GriefDefender") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize GriefDefender!");
                }else {
                    integration.add(new GriefDefender());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>GriefDefender <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.PlotSquared",false)){
                if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize PlotSquared!");
                }else {
                    integration.add(new PlotSquared());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>PlotSquared <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.Towny",false)){
                if(Bukkit.getPluginManager().getPlugin("Towny") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize Towny!");
                }else {
                    integration.add(new Towny());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>Towny <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.Lands",false)){
                if(Bukkit.getPluginManager().getPlugin("Lands") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize Lands!");
                }else {
                    integration.add(new Lands());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>Lands <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.GriefPrevention",false)){
                if(Bukkit.getPluginManager().getPlugin("GriefPrevention") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize GriefPrevention!");
                }else {
                    integration.add(new GriefPrevention());
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>GriefPrevention <color:#FFEBCD>Hooked!");
                }
            }

            skillXP = null;

            if(config.getBoolean("config.integration.mcMMO",false)){
                if(Bukkit.getPluginManager().getPlugin("mcMMO") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize mcMMO!");
                }else {
                    skillXP = new mcMMO();
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>mcMMO <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.AureliumSkills",false)){
                if(Bukkit.getPluginManager().getPlugin("AureliumSkills") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize AureliumSkills!");
                }else {
                    skillXP = new Aurelium();
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>AureliumSkills <color:#FFEBCD>Hooked!");
                }
            }
            if(config.getBoolean("config.integration.MMOCore",false)){
                if(Bukkit.getPluginManager().getPlugin("MMOCore") == null){
                    CustomCrops.instance.getLogger().warning("Failed to initialize MMOCore!");
                }else {
                    skillXP = new MMOCore();
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>MMOCore <color:#FFEBCD>Hooked!");
                }
            }
        }
    }

    public static class Basic{

        public static String pot;
        public static String watered_pot;
        public static String glass;
        public static String dead;
        public static String soilDetector;
        public static boolean hasWaterLore;
        public static String waterLeft;
        public static String waterFull;
        public static String waterEmpty;
        public static String waterRight;
        public static List<String> waterLore;

        public static void loadBasic(){
            YamlConfiguration config = getConfig("basic.yml");
            pot = config.getString("basic.pot","customcrops:pot");
            watered_pot = config.getString("basic.watered-pot","customcrops:watered_pot");
            glass = config.getString("basic.greenhouse-glass","customcrops:greenhouse_glass");
            dead = config.getString("basic.dead-crop","customcrops:crop_stage_death");
            soilDetector = StringUtils.split(config.getString("basic.soil-detector","customcrops:soil_detector"),":")[1];

            hasWaterLore = config.getBoolean("lore.watering-can.enable",false);
            if (hasWaterLore){
                waterLeft = config.getString("lore.watering-can.left");
                waterFull = config.getString("lore.watering-can.full");
                waterEmpty = config.getString("lore.watering-can.empty");
                waterRight = config.getString("lore.watering-can.right");
                waterLore = config.getStringList("lore.watering-can.lore");
            }

            CANS.clear();
            if (config.contains("water-can")){
                config.getConfigurationSection("water-can").getKeys(false).forEach(key -> {
                    if (key.equals(StringUtils.split(config.getString("water-can." + key + ".item"),":")[1])){
                        int width = config.getInt("water-can." + key + ".width");
                        if (width % 2 == 0){
                            AdventureManager.consoleMessage("<red>[CustomCrops] Watering Can " + key + "'s width should be odd!</red>");
                            return;
                        }
                        WateringCan wateringCan = new WateringCan(config.getInt("water-can." + key + ".max"), width, config.getInt("water-can." + key + ".length"));
                        CANS.put(key, wateringCan);
                    }else {
                        AdventureManager.consoleMessage("<red>[CustomCrops] Watering Can " + key + "'s key should be the same</red>");
                    }
                });
            }
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><white>" + CANS.size() + " <color:#FFEBCD>cans loaded!");

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
                        AdventureManager.consoleMessage("<red>[CustomCrops] Sprinkler " + key + "'s key should be the same with ItemsAdder 3D sprinkler's key</red>");
                    }
                });
            }
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><white>" + SPRINKLERS.size()/2 + "<color:#FFEBCD> srpinklers loaded!");
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
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>Season Change mode: <gold>Auto");
                }else {
                    AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>Season Change mode: <gold>Command");
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

            YamlConfiguration config = getConfig("messages/messages_" + Config.lang +".yml");
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
            noSeason = config.getString("messages.no-season","Season Disabled");

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
                AdventureManager.consoleMessage("<red>[CustomCrops] You forget to set " + key +"'s amount!</red>");
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
            if (config.contains("crops." + key + ".commands")){
                cropInstance.setCommands(config.getStringList("crops." + key + ".commands"));
            }
            if (config.contains("crops." + key + ".skill-xp")){
                cropInstance.setSkillXP(config.getDouble("crops." + key + ".skill-xp"));
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
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><white>" + CROPS.size() + " <color:#FFEBCD>crops loaded!");
    }

    public static void fertilizerLoad(){
        FERTILIZERS.clear();
        YamlConfiguration config = getConfig("fertilizer.yml");
        if (config.contains("speed")){
            config.getConfigurationSection("speed").getKeys(false).forEach(key -> {
                if (StringUtils.split(config.getString("speed." + key + ".item"), ":")[1].equals(key)){
                    SpeedGrow speedGrow = new SpeedGrow(key, config.getInt("speed." + key + ".times"), config.getDouble("speed." + key + ".chance"), config.getBoolean("加速肥料." + key + ".before-plant"));
                    speedGrow.setName(config.getString("speed." + key + ".name"));
                    FERTILIZERS.put(key, speedGrow);
                }else {
                    AdventureManager.consoleMessage("<red>[CustomCrops] fertilizer " + key + "'s key should be the same with ItemsAdder item's key </red>");
                }
            });
        }
        if (config.contains("retaining")){
            config.getConfigurationSection("retaining").getKeys(false).forEach(key -> {
                if (StringUtils.split(config.getString("retaining." + key + ".item"), ":")[1].equals(key)){
                    RetainingSoil retainingSoil = new RetainingSoil(key, config.getInt("retaining." + key + ".times"), config.getDouble("retaining." + key + ".chance"), config.getBoolean("保湿肥料." + key + ".before-plant"));
                    retainingSoil.setName(config.getString("retaining." + key + ".name"));
                    FERTILIZERS.put(key, retainingSoil);
                }else {
                    AdventureManager.consoleMessage("<red>[CustomCrops] fertilizer " + key + "'s key should be the same with ItemsAdder item's key </red>");
                }
            });
        }
        if (config.contains("quality")){
            config.getConfigurationSection("quality").getKeys(false).forEach(key -> {
                if (StringUtils.split(config.getString("quality." + key + ".item"), ":")[1].equals(key)){
                    String[] split = StringUtils.split(config.getString("quality." + key + ".chance"), "/");
                    int[] weight = new int[3];
                    weight[0] = Integer.parseInt(split[0]);
                    weight[1] = Integer.parseInt(split[1]);
                    weight[2] = Integer.parseInt(split[2]);
                    QualityCrop qualityCrop = new QualityCrop(key, config.getInt("quality." + key + ".times"), weight, config.getBoolean("quality." + key + ".before-plant"));
                    qualityCrop.setName(config.getString("quality." + key + ".name"));
                    FERTILIZERS.put(key, qualityCrop);
                }else {
                    AdventureManager.consoleMessage("<red>[CustomCrops] fertilizer " + key + "'s key should be the same with ItemsAdder item's key </red>");
                }
            });
        }
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
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><white>" + FERTILIZERS.size() + " <color:#FFEBCD>fertilizers loaded!");
    }

    public static class Sounds{

        public static Key waterPotKey;
        public static net.kyori.adventure.sound.Sound.Source waterPotSource;

        public static Key addWaterToCanKey;
        public static net.kyori.adventure.sound.Sound.Source addWaterToCanSource;

        public static Key addWaterToSprinklerKey;
        public static net.kyori.adventure.sound.Sound.Source addWaterToSprinklerSource;

        public static Key placeSprinklerKey;
        public static net.kyori.adventure.sound.Sound.Source placeSprinklerSource;

        public static Key plantSeedKey;
        public static net.kyori.adventure.sound.Sound.Source plantSeedSource;

        public static Key useFertilizerKey;
        public static net.kyori.adventure.sound.Sound.Source useFertilizerSource;

        public static Key harvestKey;
        public static net.kyori.adventure.sound.Sound.Source harvestSource;

        public static void loadSound(){
            YamlConfiguration config = getConfig("sounds.yml");

            waterPotKey = Key.key(config.getString("water-pot.sound", "minecraft:block.water.ambient"));
            waterPotSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("water-pot.type","player").toUpperCase());

            addWaterToCanKey = Key.key(config.getString("add-water-to-can.sound", "minecraft:item.bucket.fill"));
            addWaterToCanSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("add-water-to-can.type","player").toUpperCase());

            addWaterToSprinklerKey = Key.key(config.getString("add-water-to-sprinkler.sound", "minecraft:item.bucket.fill"));
            addWaterToSprinklerSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("add-water-to-sprinkler.type","player").toUpperCase());

            placeSprinklerKey = Key.key(config.getString("place-sprinkler.sound", "minecraft:block.bone_block.place"));
            placeSprinklerSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("place-sprinkler.type","player").toUpperCase());

            plantSeedKey = Key.key(config.getString("plant-seed.sound", "minecraft:item.hoe.till"));
            plantSeedSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("plant-seed.type","player").toUpperCase());

            useFertilizerKey = Key.key(config.getString("use-fertilizer.sound", "minecraft:item.hoe.till"));
            useFertilizerSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("use-fertilizer.type","player").toUpperCase());

            harvestKey = Key.key(config.getString("harvest.sound", "minecraft:block.crop.break"));
            harvestSource = net.kyori.adventure.sound.Sound.Source.valueOf(config.getString("harvest.type", "player").toUpperCase());
        }
    }
}
