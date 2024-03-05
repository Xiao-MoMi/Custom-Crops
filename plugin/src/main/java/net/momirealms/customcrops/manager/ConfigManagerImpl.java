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

package net.momirealms.customcrops.manager;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManagerImpl extends ConfigManager {

    private static final String configVersion = "35";
    private CustomCropsPlugin plugin;
    private String lang;
    private int maximumPoolSize;
    private int corePoolSize;
    private int keepAliveTime;
    private boolean debug;
    private boolean metrics;
    private boolean legacyColorSupport;
    private boolean protectLore;
    private String[] itemDetectionOrder;
    private boolean checkUpdate;
    private double[] defaultQualityRatio;

    public ConfigManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");

        debug = config.getBoolean("debug");
        metrics = config.getBoolean("metrics");
        lang = config.getString("lang");
        checkUpdate = config.getBoolean("update-checker", true);

        ConfigurationSection otherSettings = config.getConfigurationSection("other-settings");
        if (otherSettings == null) {
            LogUtils.severe("other-settings section should not be null");
            return;
        }

        maximumPoolSize = otherSettings.getInt("thread-pool-settings.maximumPoolSize", 10);
        corePoolSize = otherSettings.getInt("thread-pool-settings.corePoolSize", 10);
        keepAliveTime = otherSettings.getInt("thread-pool-settings.keepAliveTime", 30);
        itemDetectionOrder = otherSettings.getStringList("item-detection-order").toArray(new String[0]);
        protectLore = otherSettings.getBoolean("protect-original-lore", false);
        legacyColorSupport = otherSettings.getBoolean("legacy-color-code-support", true);

        ConfigurationSection mechanics = config.getConfigurationSection("mechanics");
        if (mechanics == null) {
            LogUtils.severe("mechanics section should not be null");
            return;
        }

        defaultQualityRatio = ConfigUtils.getQualityRatio(mechanics.getString("default-quality-ratio", "17/2/1"));
    }

    @Override
    public void unload() {

    }

    @Override
    public boolean hasLegacyColorSupport() {
        return legacyColorSupport;
    }

    @Override
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    @Override
    public int getCorePoolSize() {
        return corePoolSize;
    }

    @Override
    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    @Override
    protected double[] getDefaultQualityRatio() {
        return defaultQualityRatio;
    }

    @Override
    protected String getLang() {
        return lang;
    }

    @Override
    public boolean getDebugMode() {
        return debug;
    }

    @Override
    public boolean isProtectLore() {
        return protectLore;
    }

    @Override
    public String[] getItemDetectionOrder() {
        return itemDetectionOrder;
    }

    @Override
    public boolean hasMetrics() {
        return metrics;
    }

    @Override
    public boolean hasCheckUpdate() {
        return checkUpdate;
    }
}
