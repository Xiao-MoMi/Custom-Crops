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

package net.momirealms.customcrops.bukkit;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.SimpleRegistryAccess;
import net.momirealms.customcrops.api.core.block.*;
import net.momirealms.customcrops.api.core.item.*;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.event.CustomCropsReloadEvent;
import net.momirealms.customcrops.api.misc.HologramManager;
import net.momirealms.customcrops.api.misc.cooldown.CoolDownManager;
import net.momirealms.customcrops.api.misc.placeholder.BukkitPlaceholderManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.bukkit.action.BlockActionManager;
import net.momirealms.customcrops.bukkit.action.PlayerActionManager;
import net.momirealms.customcrops.bukkit.command.BukkitCommandManager;
import net.momirealms.customcrops.bukkit.config.BukkitConfigManager;
import net.momirealms.customcrops.bukkit.integration.BukkitIntegrationManager;
import net.momirealms.customcrops.bukkit.item.BukkitItemManager;
import net.momirealms.customcrops.bukkit.requirement.BlockRequirementManager;
import net.momirealms.customcrops.bukkit.requirement.PlayerRequirementManager;
import net.momirealms.customcrops.bukkit.scheduler.BukkitSchedulerAdapter;
import net.momirealms.customcrops.bukkit.sender.BukkitSenderFactory;
import net.momirealms.customcrops.bukkit.world.BukkitWorldManager;
import net.momirealms.customcrops.common.config.ConfigLoader;
import net.momirealms.customcrops.common.dependency.Dependency;
import net.momirealms.customcrops.common.dependency.DependencyManagerImpl;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.locale.TranslationManager;
import net.momirealms.customcrops.common.plugin.classpath.ClassPathAppender;
import net.momirealms.customcrops.common.plugin.classpath.ReflectionClassPathAppender;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import net.momirealms.customcrops.common.plugin.logging.JavaPluginLogger;
import net.momirealms.customcrops.common.plugin.logging.PluginLogger;
import net.momirealms.sparrow.heart.SparrowHeart;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class BukkitCustomCropsPluginImpl extends BukkitCustomCropsPlugin {

    private final ClassPathAppender classPathAppender;
    private final PluginLogger logger;
    private BukkitCommandManager commandManager;
    private HologramManager hologramManager;
    private Consumer<Object> debugger;
    private String buildByBit = "%%__BUILTBYBIT__%%";
    private String polymart = "%%__POLYMART__%%";
    private String time = "%%__TIMESTAMP__%%";
    private String user = "%%__USER__%%";
    private String username = "%%__USERNAME__%%";

    public BukkitCustomCropsPluginImpl(Plugin boostrap) {
        super(boostrap);
        VersionHelper.init(getServerVersion());
        this.scheduler = new BukkitSchedulerAdapter(this);
        this.logger = new JavaPluginLogger(getBoostrap().getLogger());
        this.classPathAppender = new ReflectionClassPathAppender(this);
        this.dependencyManager = new DependencyManagerImpl(this);
        this.registryAccess = new SimpleRegistryAccess(this);
    }

    @Override
    public void debug(Object message) {
        if (this.debugger != null)
            this.debugger.accept(message);
    }

    @Override
    public InputStream getResourceStream(String filePath) {
        return getBoostrap().getResource(filePath);
    }

    @Override
    public PluginLogger getPluginLogger() {
        return logger;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return classPathAppender;
    }

    @Override
    public Path getDataDirectory() {
        return getBoostrap().getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public ConfigLoader getConfigManager() {
        return configManager;
    }

    @Override
    public String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion().split("-")[0];
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getPluginVersion() {
        return getBoostrap().getDescription().getVersion();
    }

    @Override
    public void load() {
        this.dependencyManager.loadDependencies(
                List.of(
                        Dependency.BOOSTED_YAML,
                        Dependency.BSTATS_BASE, Dependency.BSTATS_BUKKIT,
                        Dependency.CAFFEINE,
                        Dependency.GEANTY_REF,
                        Dependency.CLOUD_CORE, Dependency.CLOUD_SERVICES, Dependency.CLOUD_BUKKIT, Dependency.CLOUD_PAPER, Dependency.CLOUD_BRIGADIER, Dependency.CLOUD_MINECRAFT_EXTRAS,
                        Dependency.GSON,
                        Dependency.EXP4J,
                        Dependency.ZSTD
                )
        );
        this.registerDefaultMechanics();
    }

    @Override
    public void enable() {
        SparrowHeart.getInstance();
        this.configManager = new BukkitConfigManager(this);
        super.requirementManagers.put(Player.class, new PlayerRequirementManager(this));
        super.requirementManagers.put(CustomCropsBlockState.class, new BlockRequirementManager(this));
        super.actionManagers.put(Player.class, new PlayerActionManager(this));
        super.actionManagers.put(CustomCropsBlockState.class, new BlockActionManager(this));
        this.translationManager = new TranslationManager(this);
        this.senderFactory = new BukkitSenderFactory(this);
        this.itemManager = new BukkitItemManager(this);
        this.integrationManager = new BukkitIntegrationManager(this);
        this.placeholderManager = new BukkitPlaceholderManager(this);
        this.coolDownManager = new CoolDownManager(this);
        this.worldManager = new BukkitWorldManager(this);
        this.hologramManager = new HologramManager(this);
        this.commandManager = new BukkitCommandManager(this);
        this.commandManager.registerDefaultFeatures();

        boolean downloadFromPolymart = polymart.equals("1");
        boolean downloadFromBBB = buildByBit.equals("true");

        this.getScheduler().sync().runLater(() -> {
            getPluginLogger().info("CustomCrops Registry has been frozen");
            ((SimpleRegistryAccess) registryAccess).freeze();
            this.reload();
            if (ConfigManager.metrics()) new Metrics((JavaPlugin) getBoostrap(), 16593);
            if (ConfigManager.checkUpdate()) {
                VersionHelper.UPDATE_CHECKER.apply(this).thenAccept(result -> {
                    String link;
                    if (downloadFromPolymart) {
                        link = "https://polymart.org/resource/2625/";
                    } else if (downloadFromBBB) {
                        link = "https://builtbybit.com/resources/36363/";
                    } else {
                        link = "https://github.com/Xiao-MoMi/Custom-Crops/";
                    }
                    if (!result) {
                        this.getPluginLogger().info("You are using the latest version.");
                    } else {
                        this.getPluginLogger().warn("Update is available: " + link);
                    }
                });
            }
        }, 1, null);
    }

    @Override
    public void disable() {
        this.worldManager.disable();
        this.placeholderManager.disable();
        this.hologramManager.disable();
        this.integrationManager.disable();
        this.coolDownManager.disable();
        this.commandManager.unregisterFeatures();
    }

    @Override
    public void reload() {

        this.worldManager.unload();

        this.configManager.reload();
        this.debugger = ConfigManager.debug() ? (s) -> logger.info("[DEBUG] " + s.toString()) : (s) -> {};
        this.coolDownManager.reload();
        this.placeholderManager.reload();
        this.translationManager.reload();
        this.hologramManager.reload();

        this.actionManagers.values().forEach(Reloadable::reload);
        this.requirementManagers.values().forEach(Reloadable::reload);

        this.worldManager.load();

        EventUtils.fireAndForget(new CustomCropsReloadEvent(this));
    }

    private void registerDefaultMechanics() {
        registryAccess.registerFertilizerType(FertilizerType.SPEED_GROW);
        registryAccess.registerFertilizerType(FertilizerType.QUALITY);
        registryAccess.registerFertilizerType(FertilizerType.SOIL_RETAIN);
        registryAccess.registerFertilizerType(FertilizerType.VARIATION);
        registryAccess.registerFertilizerType(FertilizerType.YIELD_INCREASE);

        registryAccess.registerBlockMechanic(new CropBlock());
        registryAccess.registerBlockMechanic(new PotBlock());
        registryAccess.registerBlockMechanic(new ScarecrowBlock());
        registryAccess.registerBlockMechanic(new SprinklerBlock());
        registryAccess.registerBlockMechanic(new GreenhouseBlock());
        registryAccess.registerBlockMechanic(new DeadCrop());

        registryAccess.registerItemMechanic(new SeedItem());
        registryAccess.registerItemMechanic(new WateringCanItem());
        registryAccess.registerItemMechanic(new FertilizerItem());
        registryAccess.registerItemMechanic(new SprinklerItem());
    }
}
