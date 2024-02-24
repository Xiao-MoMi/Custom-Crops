package net.momirealms.customcrops.manager;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.VersionManager;

public class VersionManagerImpl extends VersionManager {

    private CustomCropsPlugin plugin;
    private final String pluginVersion;
    private final String serverVersion;
    private boolean hasRegionScheduler;
    private final boolean isSpigot;

    public VersionManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.isSpigot = plugin.getServer().getName().equals("CraftBukkit");
        this.pluginVersion = plugin.getDescription().getVersion();
        this.serverVersion = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            this.hasRegionScheduler = true;
        } catch (ClassNotFoundException ignored) {
            this.hasRegionScheduler = false;
        }
    }

    @Override
    public boolean hasRegionScheduler() {
        return hasRegionScheduler;
    }

    @Override
    public String getPluginVersion() {
        return pluginVersion;
    }

    @Override
    public String getServerVersion() {
        return serverVersion;
    }

    @Override
    public boolean isSpigot() {
        return isSpigot;
    }

    @Override
    public boolean isVersionNewerThan1_19_R3() {
        return false;
    }
}
