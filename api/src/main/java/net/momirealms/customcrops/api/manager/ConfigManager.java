package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;

public abstract class ConfigManager implements Reloadable {

    private static ConfigManager instance;

    public ConfigManager() {
        instance = this;
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public static boolean legacyColorSupport() {
        return instance.hasLegacyColorSupport();
    }

    public static int maximumPoolSize() {
        return instance.getMaximumPoolSize();
    }

    public static int corePoolSize() {
        return instance.getCorePoolSize();
    }

    public static int keepAliveTime() {
        return instance.getKeepAliveTime();
    }

    public static boolean debug() {
        return instance.getDebugMode();
    }

    public static boolean protectLore() {
        return instance.isProtectLore();
    }

    public static String[] itemDetectionOrder() {
        return instance.getItemDetectionOrder();
    }

    protected abstract boolean getDebugMode();

    protected abstract boolean hasLegacyColorSupport();

    protected abstract int getMaximumPoolSize();

    protected abstract int getKeepAliveTime();

    protected abstract int getCorePoolSize();

    public abstract boolean isProtectLore();

    public abstract String[] getItemDetectionOrder();
}
