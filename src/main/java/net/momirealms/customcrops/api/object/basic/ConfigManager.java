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

package net.momirealms.customcrops.api.object.basic;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.util.AdventureUtils;
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ConfigManager extends Function {

    public static String lang;
    public static boolean enableBStats;
    public static boolean checkUpdate;
    public static boolean enableSkillBonus;
    public static String bonusFormula;
    public static int greenhouseRange;
    public static boolean whiteListWorlds;
    public static HashSet<String> worldList;
    public static String worldFolderPath;
    public static boolean debugScheduler;
    public static boolean debugCorruption;
    public static String greenhouseBlock;
    public static String scarecrow;
    public static boolean enableGreenhouse;
    public static int pointGainInterval;
    public static int corePoolSize;
    public static double[] defaultRatio;
    public static int maxPoolSize;
    public static long keepAliveTime;
    public static int seasonInterval;
    public static boolean enableSeason;
    public static boolean rsHook;
    public static boolean enableScheduleSystem;
    public static boolean syncSeason;
    public static boolean autoSeasonChange;
    public static String referenceWorld;
    public static boolean enableLimitation;
    public static int maxCropPerChunk;
    public static int cacheSaveInterval;
    public static int intervalConsume;
    public static int intervalWork;
    public static int fixRange;
    public static boolean disableMoistureMechanic;
    public static boolean preventTrampling;
    public static boolean onlyInLoadedChunks;
    public static boolean enableCorruptionFixer;
    public static boolean debugWorld;
    public static boolean updateDuringLoading;

    private final HashMap<String, Integer> cropPerWorld;
    private final CustomCrops plugin;

    public ConfigManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.cropPerWorld = new HashMap<>();
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");
        onlyInLoadedChunks = config.getBoolean("mechanics.only-work-in-loaded-chunks", false);
    }

    @Override
    public void load() {
        this.loadConfig();
    }

    @Override
    public void unload() {
        this.cropPerWorld.clear();
    }

    private void loadConfig() {
        if (new File(plugin.getDataFolder(), "config.yml").exists()) ConfigUtils.update("config.yml");
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");
        enableBStats = config.getBoolean("metrics");
        lang = config.getString("lang");
        debugScheduler = config.getBoolean("debug.log-scheduler", false);
        debugCorruption = config.getBoolean("debug.log-corruption-fixer", false);
        debugWorld = config.getBoolean("debug.log-world-state", false);
        loadWorlds(Objects.requireNonNull(config.getConfigurationSection("worlds")));
        loadScheduleSystem(Objects.requireNonNull(config.getConfigurationSection("schedule-system")));
        loadMechanic(Objects.requireNonNull(config.getConfigurationSection("mechanics")));
        loadOtherSetting(Objects.requireNonNull(config.getConfigurationSection("other-settings")));
        loadOptimization(Objects.requireNonNull(config.getConfigurationSection("optimization")));
    }

    private void loadOptimization(ConfigurationSection section) {
        enableLimitation = section.getBoolean("limitation.growing-crop-amount.enable", true);
        maxCropPerChunk = section.getInt("limitation.growing-crop-amount.default", 64);
        updateDuringLoading = !ConfigManager.onlyInLoadedChunks && section.getBoolean("only-update-during-chunk-loading", false);
        List<String> worldSettings = section.getStringList("limitation.growing-crop-amount.worlds");
        for (String setting : worldSettings) {
            String[] split = setting.split(":", 2);
            try {
                cropPerWorld.put(split[0], Integer.parseInt(split[1]));
            } catch (NumberFormatException e) {
                AdventureUtils.consoleMessage("<red>[CustomCrops] Wrong number format found at: optimization.limitation.growing-crop-amount.worlds in config.yml");
            }
        }
    }

    private void loadWorlds(ConfigurationSection section) {
        worldFolderPath = section.getString("absolute-world-folder-path", "");
        whiteListWorlds = section.getString("mode", "whitelist").equalsIgnoreCase("whitelist");
        worldList = new HashSet<>(section.getStringList("list"));
    }

    private void loadScheduleSystem(ConfigurationSection section) {
        enableScheduleSystem = section.getBoolean("enable", true);
        pointGainInterval = section.getInt("point-gain-interval", 600);
        corePoolSize = section.getInt("thread-pool-settings.corePoolSize", 2);
        maxPoolSize = section.getInt("thread-pool-settings.maximumPoolSize", 4);
        keepAliveTime = section.getInt("thread-pool-settings.keepAliveTime", 10);
        cacheSaveInterval = section.getInt("cache-save-interval", 12000);
        intervalConsume = section.getInt("consume-water-fertilizer-every-x-point", 2);
        intervalWork = section.getInt("sprinkler-work-every-x-point", 2);
    }

    private void loadMechanic(ConfigurationSection section) {
        defaultRatio = ConfigUtils.getQualityRatio(section.getString("default-quality-ratio", "17/2/1"));
        enableSeason = section.getBoolean("season.enable", true);
        syncSeason = section.getBoolean("season.sync-season.enable", false);
        referenceWorld = section.getString("season.sync-season.reference");
        autoSeasonChange = section.getBoolean("season.auto-season-change.enable");
        seasonInterval = section.getInt("season.auto-season-change.duration", 28);
        enableGreenhouse = section.getBoolean("season.greenhouse.enable", true);
        greenhouseRange = section.getInt("season.greenhouse.range", 5);
        greenhouseBlock = section.getString("season.greenhouse.block");
        scarecrow = section.getString("scarecrow");
        disableMoistureMechanic = section.getBoolean("vanilla-farmland.disable-moisture-mechanic", false);
        preventTrampling = section.getBoolean("vanilla-farmland.prevent-trampling", false);
    }

    private void loadOtherSetting(ConfigurationSection section) {
        enableSkillBonus = section.getBoolean("skill-bonus.enable", false);
        bonusFormula =  section.getString("skill-bonus.formula");
        enableCorruptionFixer =  section.getBoolean("enable-corruption-fixer", true);
        fixRange =  section.getInt("corrupt-fix-range", 4);
    }

    public int getCropLimit(String world) {
        return Objects.requireNonNullElse(cropPerWorld.get(world), maxCropPerChunk);
    }
}
