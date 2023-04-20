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
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
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
    public static boolean debug;
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
    public static boolean setUpMode;

    private final CustomCrops plugin;

    public ConfigManager(CustomCrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        this.loadConfig();
    }

    private void loadConfig() {
        if (new File(plugin.getDataFolder(), "config.yml").exists()) ConfigUtils.update("config.yml");
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");
        enableBStats = config.getBoolean("metrics");
        lang = config.getString("lang");
        debug = config.getBoolean("debug");
        setUpMode = config.getBoolean("set-up-mode", true);
        loadWorlds(Objects.requireNonNull(config.getConfigurationSection("worlds")));
        loadOptimization(Objects.requireNonNull(config.getConfigurationSection("optimization")));
        loadScheduleSystem(Objects.requireNonNull(config.getConfigurationSection("schedule-system")));
        loadMechanic(Objects.requireNonNull(config.getConfigurationSection("mechanics")));
        loadOtherSetting(Objects.requireNonNull(config.getConfigurationSection("other-settings")));
    }

    private void loadOptimization(ConfigurationSection section) {
        enableLimitation = section.getBoolean("limitation.enable");
        maxCropPerChunk = section.getInt("limitation.valid-crop-amount");
    }

    private void loadWorlds(ConfigurationSection section) {
        worldFolderPath = section.getString("folder", "");
        whiteListWorlds = section.getString("mode", "whitelist").equalsIgnoreCase("whitelist");
        worldList = new HashSet<>(section.getStringList("list"));
    }

    private void loadScheduleSystem(ConfigurationSection section) {
        enableScheduleSystem = section.getBoolean("default-schedule");
        pointGainInterval = section.getInt("point-gain-interval", 1000);
        corePoolSize = section.getInt("thread-pool-settings.corePoolSize", 2);
        maxPoolSize = section.getInt("thread-pool-settings.maximumPoolSize", 4);
        keepAliveTime = section.getInt("thread-pool-settings.keepAliveTime", 10);
        cacheSaveInterval = section.getInt("cache-save-interval", 7200);
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
    }

    private void loadOtherSetting(ConfigurationSection section) {
        enableSkillBonus = section.getBoolean("skill-bonus.enable", false);
        bonusFormula =  section.getString("skill-bonus.formula");
    }
}
