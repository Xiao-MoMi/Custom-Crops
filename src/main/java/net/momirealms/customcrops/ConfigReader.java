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
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.helper.Log;
import net.momirealms.customcrops.integrations.protection.*;
import net.momirealms.customcrops.integrations.skill.*;
import net.momirealms.customcrops.objects.Crop;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WateringCan;
import net.momirealms.customcrops.objects.fertilizer.*;
import net.momirealms.customcrops.requirements.Biome;
import net.momirealms.customcrops.requirements.Permission;
import net.momirealms.customcrops.requirements.Requirement;
import net.momirealms.customcrops.requirements.YPos;
import net.momirealms.customcrops.utils.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigReader {

    public static HashMap<String, Crop> CROPS = new HashMap<>();
    public static HashMap<String, Fertilizer> FERTILIZERS = new HashMap<>();
    public static HashMap<String, WateringCan> CANS = new HashMap<>();
    public static HashMap<String, Sprinkler> SPRINKLERS = new HashMap<>();
    public static boolean useRedis;

    public static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomCrops.plugin.getDataFolder(), configName);
        if (!file.exists()) CustomCrops.plugin.saveResource(configName, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void reloadConfig(){
        Sounds.loadSound();
        Config.loadConfig();
        Season.loadSeason();
        Message.loadMessage();
        Basic.loadBasic();
        fertilizerLoad();
        cropLoad();
    }

    public static class Config{

        public static List<World> worlds;
        public static List<String> worldNames;
        public static List<Long> cropGrowTimeList;
        public static List<Integration> integration;
        public static String referenceWorld;
        public static String lang;
        public static String version;
        public static String cropMode;
        public static int cropLimit;
        public static int sprinklerLimit;
        public static int yMin;
        public static int yMax;
        public static int sprinklerRefill;
        public static int waterCanRefill;
        public static int timeToGrow;
        public static int timeToWork;
        public static int growMode;
        public static boolean asyncCheck;
        public static boolean enableLimit;
        public static boolean hasParticle;
        public static boolean rightClickHarvest;
        public static boolean quality;
        public static boolean canAddWater;
        public static boolean allWorld;
        public static boolean pwSeason;
        public static boolean nwSeason;
        public static boolean needEmptyHand;
        public static boolean boneMeal;
        public static boolean realisticSeason;
        public static boolean rotation;
        public static boolean variant4;
        public static boolean oneTry;
        public static double boneMealChance;
        public static double quality_1;
        public static double quality_2;
        public static SkillXP skillXP;
        public static Particle boneMealSuccess;

        public static void loadConfig(){

            CustomCrops.plugin.saveDefaultConfig();
            CustomCrops.plugin.reloadConfig();
            FileConfiguration config = CustomCrops.plugin.getConfig();

            lang = config.getString("config.lang","chinese");

            cropGrowTimeList = config.getLongList("config.grow-time");
            cropGrowTimeList.forEach(time -> {if(time < 0 || time > 23999){AdventureManager.consoleMessage("<red>[CustomCrops] Grow time should be between 0 and 23999");}});
            timeToGrow = config.getInt("config.time-to-grow",60)*20;
            timeToWork = config.getInt("config.time-to-work",30)*20;
            asyncCheck = config.getBoolean("config.async-time-check",false);
            growMode = config.getInt("config.grow-mode",3); if (growMode > 4 || growMode < 1) growMode = 3;
            allWorld = config.getBoolean("config.all-world-grow",false);
            hasParticle = config.getBoolean("config.water-particles", true);
            rightClickHarvest = config.getBoolean("config.right-click-harvest", true);
            needEmptyHand = config.getBoolean("config.harvest-with-empty-hand", true);
            pwSeason = config.getBoolean("config.prevent-plant-if-wrong-season", true);
            nwSeason = config.getBoolean("config.should-notify-if-wrong-season", true);
            rotation = config.getBoolean("config.rotation.enable", false);
            oneTry = config.getBoolean("config.gigantic-only-one-try", false);
            variant4 = config.getInt("config.rotation.variant", 4) == 4;

            boneMeal = config.getBoolean("config.bone-meal.enable", true);
            if (boneMeal){
                boneMealChance = config.getDouble("config.bone-meal.chance",0.5);
                boneMealSuccess = Particle.valueOf(config.getString("config.bone-meal.success-particle", "VILLAGER_HAPPY").toUpperCase());
            }

            enableLimit = config.getBoolean("config.limit.enable",true);
            if (enableLimit){
                cropLimit = config.getInt("config.limit.crop",64);
                sprinklerLimit = config.getInt("config.limit.sprinkler",16);
            }

            String serverVersion = Bukkit.getServer().getClass().getPackage().getName();
            if (serverVersion.contains("16") || serverVersion.contains("17")){
                yMin = 0; yMax = 256;
            }else {
                yMin = -64; yMax = 320;
            }

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
            version = config.getString("config-version");
            cropMode = config.getString("config.crop-mode","tripwire");
            canAddWater = config.getBoolean("config.water-can-add-water-to-sprinkler",true);

            if (allWorld){
                if (config.getStringList("config.whitelist-worlds").size() > 1) AdventureManager.consoleMessage("<red>[CustomCrops] Only one whitelist world is allowed when \"all-world-grow\" enabled!");
                referenceWorld = config.getStringList("config.whitelist-worlds").get(0);
            }

            worlds = new ArrayList<>();
            worldNames = config.getStringList("config.whitelist-worlds");
            worldNames.forEach(worldName -> {
                World world = Bukkit.getWorld(worldName);
                if (world == null) AdventureManager.consoleMessage("<red>[CustomCrops] World " + worldName + " doesn't exist");
                else worlds.add(world);
            });

            integration = new ArrayList<>();
            if (config.getBoolean("config.integration.Residence",false)){
                if (Bukkit.getPluginManager().getPlugin("Residence") == null) Log.warn("Failed to initialize Residence!");
                else {integration.add(new ResidenceIntegration());hookMessage("Residence");}
            }
            if (config.getBoolean("config.integration.Kingdoms",false)){
                if (Bukkit.getPluginManager().getPlugin("Kingdoms") == null) Log.warn("Failed to initialize Kingdoms!");
                else {integration.add(new KingdomsXIntegration());hookMessage("Kingdoms");}
            }
            if (config.getBoolean("config.integration.WorldGuard",false)){
                if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) Log.warn("Failed to initialize WorldGuard!");
                else {integration.add(new WorldGuardIntegration());hookMessage("WorldGuard");}
            }
            if (config.getBoolean("config.integration.GriefDefender",false)){
                if(Bukkit.getPluginManager().getPlugin("GriefDefender") == null) Log.warn("Failed to initialize GriefDefender!");
                else {integration.add(new GriefDefenderIntegration());hookMessage("GriefDefender");}
            }
            if (config.getBoolean("config.integration.PlotSquared",false)){
                if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null) Log.warn("Failed to initialize PlotSquared!");
                else {integration.add(new PlotSquaredIntegration());hookMessage("PlotSquared");}
            }
            if (config.getBoolean("config.integration.Towny",false)){
                if (Bukkit.getPluginManager().getPlugin("Towny") == null) Log.warn("Failed to initialize Towny!");
                else {integration.add(new TownyIntegration());hookMessage("Towny");}
            }
            if (config.getBoolean("config.integration.Lands",false)){
                if (Bukkit.getPluginManager().getPlugin("Lands") == null) Log.warn("Failed to initialize Lands!");
                else {integration.add(new LandsIntegration());hookMessage("Lands");}
            }
            if (config.getBoolean("config.integration.GriefPrevention",false)){
                if (Bukkit.getPluginManager().getPlugin("GriefPrevention") == null) Log.warn("Failed to initialize GriefPrevention!");
                else {integration.add(new GriefPreventionIntegration());hookMessage("GriefPrevention");}
            }
            if (config.getBoolean("config.integration.CrashClaim",false)){
                if (Bukkit.getPluginManager().getPlugin("CrashClaim") == null) Log.warn("Failed to initialize CrashClaim!");
                else {integration.add(new CrashClaimIntegration());hookMessage("CrashClaim");}
            }
            if (config.getBoolean("config.integration.BentoBox",false)){
                if (Bukkit.getPluginManager().getPlugin("BentoBox") == null) Log.warn("Failed to initialize BentoBox!");
                else {integration.add(new BentoBoxIntegration());hookMessage("BentoBox");}
            }

            realisticSeason = false;
            if (config.getBoolean("config.integration.RealisticSeasons",false)){
                if (Bukkit.getPluginManager().getPlugin("RealisticSeasons") == null) Log.warn("Failed to initialize RealisticSeasons!");
                else {realisticSeason = true;hookMessage("RealisticSeasons");}
            }

            skillXP = null;
            if (config.getBoolean("config.integration.mcMMO",false)){
                if (Bukkit.getPluginManager().getPlugin("mcMMO") == null) Log.warn("Failed to initialize mcMMO!");
                else {skillXP = new mcMMOIntegration();hookMessage("mcMMO");}
            }
            if (config.getBoolean("config.integration.AureliumSkills",false)){
                if (Bukkit.getPluginManager().getPlugin("AureliumSkills") == null) Log.warn("Failed to initialize AureliumSkills!");
                else {skillXP = new AureliumIntegration();hookMessage("AureliumSkills");}
            }
            if(config.getBoolean("config.integration.MMOCore",false)){
                if(Bukkit.getPluginManager().getPlugin("MMOCore") == null) Log.warn("Failed to initialize MMOCore!");
                else {skillXP = new MMOCoreIntegration();hookMessage("MMOCore");}
            }
            if(config.getBoolean("config.integration.EcoSkills",false)){
                if(Bukkit.getPluginManager().getPlugin("EcoSkills") == null) Log.warn("Failed to initialize EcoSkills!");
                else {skillXP = new EcoSkillsIntegration();hookMessage("EcoSkills");}
            }
            if(config.getBoolean("config.integration.JobsReborn",false)){
                if(Bukkit.getPluginManager().getPlugin("Jobs") == null) Log.warn("Failed to initialize Jobs!");
                else {skillXP = new JobsRebornIntegration();hookMessage("JobsReborn");}
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
            soilDetector = config.getString("basic.soil-detector","customcrops:soil_detector");

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
                    int width = config.getInt("water-can." + key + ".width");
                    if (width % 2 == 0){
                        AdventureManager.consoleMessage("<red>[CustomCrops] Watering Can " + key + "'s width should be odd!</red>");
                        return;
                    }
                    String namespacedID = config.getString("water-can." + key + ".item");
                    WateringCan wateringCan = new WateringCan(config.getInt("water-can." + key + ".max"), width, config.getInt("water-can." + key + ".length"));
                    CANS.put(namespacedID, wateringCan);
                });
            }
            AdventureManager.consoleMessage("[CustomCrops] Loaded <green>" + CANS.size() + " <gray>watering-cans");

            SPRINKLERS.clear();
            if (config.contains("sprinkler")){
                config.getConfigurationSection("sprinkler").getKeys(false).forEach(key -> {
                    Sprinkler sprinklerData = new Sprinkler(config.getInt("sprinkler." + key + ".range"), config.getInt("sprinkler." + key + ".max-water"));
                    String threeD = config.getString("sprinkler." + key + ".3Ditem");
                    sprinklerData.setNamespacedID_2(threeD);
                    String twoD = config.getString("sprinkler." + key + ".2Ditem");
                    sprinklerData.setNamespacedID_1(twoD);
                    SPRINKLERS.put(threeD, sprinklerData);
                    SPRINKLERS.put(twoD, sprinklerData);
                });
            }
            AdventureManager.consoleMessage("[CustomCrops] Loaded <green>" + SPRINKLERS.size()/2 + "<gray> sprinklers");
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
                if (Config.growMode == 4) AdventureManager.consoleMessage("<red>[CustomCrops] Warining: It's not advised to enable season in mode 4</red>");
                greenhouse = config.getBoolean("season.greenhouse.enable",false);
                if (greenhouse) range = config.getInt("season.greenhouse.range",7);
                seasonChange = config.getBoolean("season.auto-season-change.enable",false);
                duration = config.getInt("season.auto-season-change.duration",28);
                if (seasonChange) AdventureManager.consoleMessage("[CustomCrops] Season Change mode: <gold>Auto");
                else AdventureManager.consoleMessage("[CustomCrops] Season Change mode: <gold>Command");
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
            Crop cropInstance;
            if (config.contains("crops." + key + ".amount")){
                String[] split = StringUtils.split(config.getString("crops." + key + ".amount"),"~");
                cropInstance = new Crop(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
            }else {
                AdventureManager.consoleMessage("<red>[CustomCrops] You forget to set " + key +"'s amount!</red>");
                return;
            }
            cropInstance.setGrowChance(config.getDouble("crops." + key + ".grow-chance", 1));
            if (config.contains("crops." + key + ".gigantic"))
                if (config.contains("crops." + key + ".gigantic.block")){
                    cropInstance.setGiant(config.getString("crops." + key + ".gigantic.block"));
                    cropInstance.setIsBlock(true);
                }
                if (config.contains("crops." + key + ".gigantic.furniture")){
                    cropInstance.setGiant(config.getString("crops." + key + ".gigantic.furniture"));
                    cropInstance.setIsBlock(false);
                }
                cropInstance.setGiantChance(config.getDouble("crops." + key + ".gigantic.chance",0.01));
            if (Season.enable && config.contains("crops." + key + ".season"))
                cropInstance.setSeasons(config.getStringList("crops." + key + ".season"));
            if (config.contains("crops." + key + ".return"))
                cropInstance.setReturnStage(config.getString("crops." + key + ".return"));
            if (config.contains("crops." + key + ".drop-other-loots"))
                cropInstance.setOtherLoots(config.getStringList("crops." + key + ".drop-other-loots"));
            if (config.contains("crops." + key + ".commands"))
                cropInstance.setCommands(config.getStringList("crops." + key + ".commands"));
            if (config.contains("crops." + key + ".skill-xp"))
                cropInstance.setSkillXP(config.getDouble("crops." + key + ".skill-xp"));
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
                cropInstance.setDropIALoot(config.getBoolean("crops." + key + ".drop-ia-loots", false));
            }else {cropInstance.setDropIALoot(false);}
            CROPS.put(key, cropInstance);
        });
        AdventureManager.consoleMessage("[CustomCrops] Loaded<green> " + CROPS.size() + " <gray>crops");
    }

    public static void fertilizerLoad(){
        FERTILIZERS.clear();
        YamlConfiguration config = getConfig("fertilizer.yml");
        if (config.contains("speed")){
            config.getConfigurationSection("speed").getKeys(false).forEach(key -> {
                String id = StringUtils.split(config.getString("speed." + key + ".item"), ":")[1];
                SpeedGrow speedGrow = new SpeedGrow(id, config.getInt("speed." + key + ".times"));
                speedGrow.setName(config.getString("speed." + key + ".name",""));
                speedGrow.setBefore(config.getBoolean("speed." + key + ".before-plant",false));
                speedGrow.setChance(config.getDouble("speed." + key + ".chance"));
                if (config.contains("speed." + key + ".particle"))
                    speedGrow.setParticle(Particle.valueOf(config.getString("speed." + key + ".particle").toUpperCase()));
                FERTILIZERS.put(id, speedGrow);
            });
        }
        if (config.contains("retaining")){
            config.getConfigurationSection("retaining").getKeys(false).forEach(key -> {
                String id = StringUtils.split(config.getString("retaining." + key + ".item"), ":")[1];
                RetainingSoil retainingSoil = new RetainingSoil(id, config.getInt("retaining." + key + ".times"));
                retainingSoil.setBefore(config.getBoolean("retaining." + key + ".before-plant",false));
                retainingSoil.setChance(config.getDouble("retaining." + key + ".chance"));
                retainingSoil.setName(config.getString("retaining." + key + ".name",""));
                if (config.contains("retaining." + key + ".particle"))
                    retainingSoil.setParticle(Particle.valueOf(config.getString("retaining." + key + ".particle").toUpperCase()));
                FERTILIZERS.put(id, retainingSoil);
            });
        }
        if (config.contains("quality")){
            config.getConfigurationSection("quality").getKeys(false).forEach(key -> {
                String id = StringUtils.split(config.getString("quality." + key + ".item"), ":")[1];
                String[] split = StringUtils.split(config.getString("quality." + key + ".chance"), "/");
                int[] weight = new int[3];
                weight[0] = Integer.parseInt(split[0]);
                weight[1] = Integer.parseInt(split[1]);
                weight[2] = Integer.parseInt(split[2]);
                QualityCrop qualityCrop = new QualityCrop(key, config.getInt("quality." + key + ".times"));
                qualityCrop.setChance(weight);
                qualityCrop.setName(config.getString("quality." + key + ".name",""));
                qualityCrop.setBefore(config.getBoolean("quality." + key + ".before-plant",false));
                if (config.contains("quality." + key + ".particle"))
                    qualityCrop.setParticle(Particle.valueOf(config.getString("quality." + key + ".particle").toUpperCase()));
                FERTILIZERS.put(id, qualityCrop);
            });
        }
        if (config.contains("quantity")){
            config.getConfigurationSection("quantity").getKeys(false).forEach(key -> {
                String id = StringUtils.split(config.getString("quantity." + key + ".item"), ":")[1];
                YieldIncreasing yieldIncreasing = new YieldIncreasing(key, config.getInt("quantity." + key + ".times",14));
                yieldIncreasing.setBonus(config.getInt("quantity." + key + ".bonus",1));
                yieldIncreasing.setName(config.getString("quantity." + key + ".name",""));
                yieldIncreasing.setBefore(config.getBoolean("quantity." + key + ".before-plant",false));
                yieldIncreasing.setChance(config.getDouble("quantity." + key + ".chance"));
                if (config.contains("quantity." + key + ".particle"))
                    yieldIncreasing.setParticle(Particle.valueOf(config.getString("quantity." + key + ".particle").toUpperCase()));
                FERTILIZERS.put(id, yieldIncreasing);
            });
        }
        AdventureManager.consoleMessage("[CustomCrops] Loaded <green>" + FERTILIZERS.size() + " <gray>fertilizers");
    }

    public static class Sounds{

        public static Key waterPotKey;
        public static Sound.Source waterPotSource;
        public static Key addWaterToCanKey;
        public static Sound.Source addWaterToCanSource;
        public static Key addWaterToSprinklerKey;
        public static Sound.Source addWaterToSprinklerSource;
        public static Key placeSprinklerKey;
        public static Sound.Source placeSprinklerSource;
        public static Key plantSeedKey;
        public static Sound.Source plantSeedSource;
        public static Key useFertilizerKey;
        public static Sound.Source useFertilizerSource;
        public static Key harvestKey;
        public static Sound.Source harvestSource;
        public static Key boneMealKey;
        public static Sound.Source boneMealSource;

        public static void loadSound(){
            YamlConfiguration config = getConfig("sounds.yml");
            waterPotKey = Key.key(config.getString("water-pot.sound", "minecraft:block.water.ambient"));
            waterPotSource = Sound.Source.valueOf(config.getString("water-pot.type","player").toUpperCase());
            addWaterToCanKey = Key.key(config.getString("add-water-to-can.sound", "minecraft:item.bucket.fill"));
            addWaterToCanSource = Sound.Source.valueOf(config.getString("add-water-to-can.type","player").toUpperCase());
            addWaterToSprinklerKey = Key.key(config.getString("add-water-to-sprinkler.sound", "minecraft:item.bucket.fill"));
            addWaterToSprinklerSource = Sound.Source.valueOf(config.getString("add-water-to-sprinkler.type","player").toUpperCase());
            placeSprinklerKey = Key.key(config.getString("place-sprinkler.sound", "minecraft:block.bone_block.place"));
            placeSprinklerSource = Sound.Source.valueOf(config.getString("place-sprinkler.type","player").toUpperCase());
            plantSeedKey = Key.key(config.getString("plant-seed.sound", "minecraft:item.hoe.till"));
            plantSeedSource = Sound.Source.valueOf(config.getString("plant-seed.type","player").toUpperCase());
            useFertilizerKey = Key.key(config.getString("use-fertilizer.sound", "minecraft:item.hoe.till"));
            useFertilizerSource = Sound.Source.valueOf(config.getString("use-fertilizer.type","player").toUpperCase());
            harvestKey = Key.key(config.getString("harvest.sound", "minecraft:block.crop.break"));
            harvestSource = Sound.Source.valueOf(config.getString("harvest.type", "player").toUpperCase());
            boneMealKey = Key.key(config.getString("bonemeal.sound", "minecraft:item.hoe.till"));
            boneMealSource = Sound.Source.valueOf(config.getString("bonemeal.type","player").toUpperCase());
        }
    }

    public static void tryEnableJedis(){
        YamlConfiguration configuration = ConfigReader.getConfig("redis.yml");
        if (configuration.getBoolean("redis.enable", false)){
            useRedis = true;
            JedisUtil.initializeRedis(configuration);
        }else {
            useRedis = false;
        }
    }

    private static void hookMessage(String plugin){
        AdventureManager.consoleMessage("[CustomCrops] <gold>" + plugin + " <color:#FFEBCD>Hooked!");
    }
}
