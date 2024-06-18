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
    protected AdventureManager adventure;
    protected MessageManager messageManager;
    protected ConditionManager conditionManager;
    protected PlaceholderManager placeholderManager;

    public CustomCropsPlugin() {
        instance = this;
    }

    public static CustomCropsPlugin getInstance() {
        return instance;
    }

    public static CustomCropsPlugin get() {
        return instance;
    }

    /* Get version manager */
    public VersionManager getVersionManager() {
        return versionManager;
    }

    /* Get config manager */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /* Get scheduler */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /* Get requirement manager */
    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

    /* Get integration manager */
    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    /* Get action manager */
    public ActionManager getActionManager() {
        return actionManager;
    }

    /* Get cool down manager */
    public CoolDownManager getCoolDownManager() {
        return coolDownManager;
    }

    /* Get world data manager */
    public WorldManager getWorldManager() {
        return worldManager;
    }

    /* Get item manager */
    public ItemManager getItemManager() {
        return itemManager;
    }

    /* Get message manager */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /* Get adventure manager */
    public AdventureManager getAdventure() {
        return adventure;
    }

    /* Get condition manager */
    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    /* Get placeholder manager */
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public abstract boolean isHookedPluginEnabled(String plugin);

    public abstract void debug(String debug);

    public abstract void reload();

    public abstract boolean doesHookedPluginExist(String plugin);

    public abstract String getServerVersion();
}
