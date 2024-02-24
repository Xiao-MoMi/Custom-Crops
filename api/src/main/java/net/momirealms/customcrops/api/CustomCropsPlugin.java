package net.momirealms.customcrops.api;

import net.momirealms.customcrops.api.manager.*;
import net.momirealms.customcrops.api.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomCropsPlugin extends JavaPlugin {

    protected static CustomCropsPlugin instance;
    protected VersionManager versionManager;
    protected ConfigManager configManager;
    protected Scheduler scheduler;
    protected RequirementManager requirementManager;
    protected ActionManager actionManager;
    protected IntegrationManager integrationManager;
    protected CoolDownManager coolDownManager;
    protected WorldManager worldManager;
    protected ItemManager itemManager;

    public CustomCropsPlugin() {
        instance = this;
    }

    public static CustomCropsPlugin getInstance() {
        return instance;
    }

    public static CustomCropsPlugin get() {
        return instance;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public CoolDownManager getCoolDownManager() {
        return coolDownManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public abstract boolean isHookedPluginEnabled(String plugin);

    public abstract void debug(String debug);
}
