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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class ConfigManagerImpl extends ConfigManager {

    public static final String configVersion = "36";
    private CustomCropsPlugin plugin;
    private String lang;
    private int maximumPoolSize;
    private int corePoolSize;
    private int keepAliveTime;
    private boolean debug;
    private boolean metrics;
    private boolean legacyColorSupport;
    private boolean protectLore;
    private String[] itemDetectionOrder = new String[0];
    private boolean checkUpdate;
    private boolean disableMoisture;
    private boolean preventTrampling;
    private boolean greenhouse;
    private boolean scarecrow;
    private double[] defaultQualityRatio;
    private String greenhouseID;
    private String scarecrowID;
    private int greenhouseRange;
    private int scarecrowRange;
    private boolean syncSeasons;
    private WeakReference<World> referenceWorld;
    private boolean convertWorldOnLoad;

    public ConfigManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (!new File(plugin.getDataFolder(), "config.yml").exists())
            ConfigUtils.getConfig("config.yml");
        // update config version
        try {
            YamlDocument.create(
                    new File(CustomCropsPlugin.getInstance().getDataFolder(), "config.yml"),
                    Objects.requireNonNull(CustomCropsPlugin.getInstance().getResource("config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .addIgnoredRoute(configVersion, "other-settings.placeholder-register", '.')
                            .build()
            );
        } catch (IOException e) {
            LogUtils.warn(e.getMessage());
        }

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
        convertWorldOnLoad = otherSettings.getBoolean("convert-on-world-load", false);

        ConfigurationSection mechanics = config.getConfigurationSection("mechanics");
        if (mechanics == null) {
            LogUtils.severe("mechanics section should not be null");
            return;
        }

        defaultQualityRatio = ConfigUtils.getQualityRatio(mechanics.getString("default-quality-ratio", "17/2/1"));
        disableMoisture = mechanics.getBoolean("vanilla-farmland.disable-moisture-mechanic", false);
        preventTrampling = mechanics.getBoolean("vanilla-farmland.prevent-trampling", false);
        greenhouse = mechanics.getBoolean("greenhouse.enable", true);
        greenhouseID = mechanics.getString("greenhouse.id");
        greenhouseRange = mechanics.getInt("greenhouse.range", 5);

        scarecrow = mechanics.getBoolean("scarecrow.enable", true);
        scarecrowID = mechanics.getString("scarecrow.id");
        scarecrowRange = mechanics.getInt("scarecrow.range", 7);

        syncSeasons = mechanics.getBoolean("sync-season.enable", true);
        if (syncSeasons) {
            referenceWorld = new WeakReference<>(Bukkit.getWorld(mechanics.getString("sync-season.reference", "world")));
        }
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
    protected boolean isConvertWorldOnLoad() {
        return convertWorldOnLoad;
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

    @Override
    public boolean isDisableMoisture() {
        return disableMoisture;
    }

    @Override
    public boolean isPreventTrampling() {
        return preventTrampling;
    }

    @Override
    public boolean isGreenhouseEnabled() {
        return greenhouse;
    }

    @Override
    public String getGreenhouseID() {
        return greenhouseID;
    }

    @Override
    public int getGreenhouseRange() {
        return greenhouseRange;
    }

    @Override
    public boolean isScarecrowEnabled() {
        return scarecrow;
    }

    @Override
    public String getScarecrowID() {
        return scarecrowID;
    }

    @Override
    public int getScarecrowRange() {
        return scarecrowRange;
    }

    @Override
    public boolean isSyncSeasons() {
        return syncSeasons;
    }

    @Override
    public World getReferenceWorld() {
        return referenceWorld.get();
    }
}
