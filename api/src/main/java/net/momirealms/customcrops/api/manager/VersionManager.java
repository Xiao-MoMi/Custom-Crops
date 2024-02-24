package net.momirealms.customcrops.api.manager;

public abstract class VersionManager {

    private static VersionManager instance;

    public VersionManager() {
        instance = this;
    }

    public static VersionManager getInstance() {
        return instance;
    }

    public abstract boolean hasRegionScheduler();

    public static boolean folia() {
        return instance.hasRegionScheduler();
    }

    public abstract String getPluginVersion();

    public static String pluginVersion() {
        return instance.getPluginVersion();
    }

    public static String serverVersion() {
        return instance.getServerVersion();
    }

    public abstract String getServerVersion();

    public static boolean spigot() {
        return instance.isSpigot();
    }

    public abstract boolean isSpigot();

    public abstract boolean isVersionNewerThan1_19_R3();
}
