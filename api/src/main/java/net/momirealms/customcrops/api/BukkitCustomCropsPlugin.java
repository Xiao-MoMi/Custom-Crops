/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.RegistryAccess;
import net.momirealms.customcrops.api.core.world.WorldManager;
import net.momirealms.customcrops.api.integration.IntegrationManager;
import net.momirealms.customcrops.api.misc.cooldown.CoolDownManager;
import net.momirealms.customcrops.api.misc.placeholder.PlaceholderManager;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.common.dependency.DependencyManager;
import net.momirealms.customcrops.common.locale.TranslationManager;
import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;
import net.momirealms.customcrops.common.plugin.scheduler.AbstractJavaScheduler;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerAdapter;
import net.momirealms.customcrops.common.sender.SenderFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BukkitCustomCropsPlugin implements CustomCropsPlugin {

    private static BukkitCustomCropsPlugin instance;
    private final Plugin bootstrap;
    protected static boolean isReloading = false;

    protected AbstractJavaScheduler<Location, World> scheduler;
    protected DependencyManager dependencyManager;
    protected TranslationManager translationManager;
    protected AbstractItemManager itemManager;
    protected PlaceholderManager placeholderManager;
    protected CoolDownManager coolDownManager;
    protected ConfigManager configManager;
    protected IntegrationManager integrationManager;
    protected WorldManager worldManager;
    protected RegistryAccess registryAccess;
    protected SenderFactory<BukkitCustomCropsPlugin, CommandSender> senderFactory;
    protected CustomCropsAPI api;

    protected final Map<Class<?>, ActionManager<?>> actionManagers = new HashMap<>();
    protected final Map<Class<?>, RequirementManager<?>> requirementManagers = new HashMap<>();

    /**
     * Constructs a new BukkitCustomCropsPlugin instance.
     *
     * @param bootstrap the plugin instance used to initialize this class
     */
    public BukkitCustomCropsPlugin(Plugin bootstrap) {
        if (!bootstrap.getName().equals("CustomCrops")) {
            throw new IllegalArgumentException("CustomCrops plugin requires custom crops plugin");
        }
        this.bootstrap = bootstrap;
        this.api = new BukkitCustomCropsAPI(this);
        instance = this;
    }

    /**
     * Retrieves the singleton instance of BukkitCustomCropsPlugin.
     *
     * @return the singleton instance
     * @throws IllegalArgumentException if the plugin is not initialized
     */
    public static BukkitCustomCropsPlugin getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("Plugin not initialized");
        }
        return instance;
    }

    /**
     * Retrieves the plugin instance used to initialize this class.
     *
     * @return the {@link Plugin} instance
     */
    public Plugin getBootstrap() {
        return bootstrap;
    }

    /**
     * Retrieves the DependencyManager.
     *
     * @return the {@link DependencyManager}
     */
    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    /**
     * Retrieves the TranslationManager.
     *
     * @return the {@link TranslationManager}
     */
    @Override
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    /**
     * Retrieves the ItemManager.
     *
     * @return the {@link AbstractItemManager}
     */
    @NotNull
    public AbstractItemManager getItemManager() {
        return itemManager;
    }

    /**
     * Retrieves the SchedulerAdapter.
     *
     * @return the {@link SchedulerAdapter}
     */
    @Override
    @NotNull
    public SchedulerAdapter<Location, World> getScheduler() {
        return scheduler;
    }

    /**
     * Retrieves the SenderFactory.
     *
     * @return the {@link SenderFactory}
     */
    @NotNull
    public SenderFactory<BukkitCustomCropsPlugin, CommandSender> getSenderFactory() {
        return senderFactory;
    }

    /**
     * Retrieves the WorldManager.
     *
     * @return the {@link WorldManager}
     */
    @NotNull
    public WorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * Retrieves the IntegrationManager.
     *
     * @return the {@link IntegrationManager}
     */
    @NotNull
    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    /**
     * Retrieves the PlaceholderManager.
     *
     * @return the {@link PlaceholderManager}
     */
    @NotNull
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    /**
     * Retrieves the CoolDownManager.
     *
     * @return the {@link CoolDownManager}
     */
    @NotNull
    public CoolDownManager getCoolDownManager() {
        return coolDownManager;
    }

    /**
     * Retrieves the RegistryAccess.
     *
     * @return the {@link RegistryAccess}
     */
    @NotNull
    public RegistryAccess getRegistryAccess() {
        return registryAccess;
    }

    /**
     * Get the API instance
     *
     * @return API
     */
    public CustomCropsAPI getAPI() {
        return api;
    }

    /**
     * Retrieves an ActionManager for a specific type.
     *
     * @param type the class type of the action
     * @return the {@link ActionManager} for the specified type
     * @throws IllegalArgumentException if the type is null
     */
    @SuppressWarnings("unchecked")
    public <T> ActionManager<T> getActionManager(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        return (ActionManager<T>) actionManagers.get(type);
    }

    /**
     * Retrieves a RequirementManager for a specific type.
     *
     * @param type the class type of the requirement
     * @return the {@link RequirementManager} for the specified type
     * @throws IllegalArgumentException if the type is null
     */
    @SuppressWarnings("unchecked")
    public <T> RequirementManager<T> getRequirementManager(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        return (RequirementManager<T>) instance.requirementManagers.get(type);
    }

    /**
     * Retrieves the config manager
     *
     * @return the config manager
     */
    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Logs a debug message using a {@link Supplier}.
     *
     * @param message the message supplier to log
     */
    public abstract void debug(Supplier<String> message);

    /**
     * Retrieves the data folder for the plugin.
     *
     * @return the data folder as a {@link File}
     */
    public File getDataFolder() {
        return bootstrap.getDataFolder();
    }

    /**
     * If the plugin is currently reloading
     *
     * @return is reloading or not
     */
    public static boolean isReloading() {
        return isReloading;
    }
}
