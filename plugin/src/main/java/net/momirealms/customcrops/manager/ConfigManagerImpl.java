package net.momirealms.customcrops.manager;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.utils.ConfigUtils;

public class ConfigManagerImpl extends ConfigManager {

    private static final String configVersion = "35";
    private CustomCropsPlugin plugin;
    private int maximumPoolSize;
    private int corePoolSize;
    private int keepAliveTime;
    private boolean debug;
    private boolean metrics;
    private boolean legacyColorSupport;
    private boolean protectLore;
    private String[] itemDetectionOrder;

    public ConfigManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        YamlDocument config = ConfigUtils.getConfig("config.yml");
        if (config == null) {
            LogUtils.severe("Failed to load config.yml");
            return;
        }

        debug = config.getBoolean("debug");
        metrics = config.getBoolean("metrics");

        Section otherSettings = config.getSection("other-settings");
        if (otherSettings == null) {
            LogUtils.severe("other-settings section should not be null");
            return;
        }

        maximumPoolSize = otherSettings.getInt("thread-pool-settings.maximumPoolSize", 10);
        corePoolSize = otherSettings.getInt("thread-pool-settings.corePoolSize", 10);
        keepAliveTime = otherSettings.getInt("thread-pool-settings.keepAliveTime", 30);
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
}
