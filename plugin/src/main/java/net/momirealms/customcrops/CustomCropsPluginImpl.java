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

package net.momirealms.customcrops;

import net.momirealms.antigrieflib.AntiGriefLib;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.event.CustomCropsReloadEvent;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.CoolDownManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.compatibility.IntegrationManagerImpl;
import net.momirealms.customcrops.libraries.classpath.ReflectionClassPathAppender;
import net.momirealms.customcrops.libraries.dependencies.Dependency;
import net.momirealms.customcrops.libraries.dependencies.DependencyManager;
import net.momirealms.customcrops.libraries.dependencies.DependencyManagerImpl;
import net.momirealms.customcrops.manager.*;
import net.momirealms.customcrops.mechanic.action.ActionManagerImpl;
import net.momirealms.customcrops.mechanic.condition.ConditionManagerImpl;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import net.momirealms.customcrops.mechanic.item.factory.BukkitItemFactory;
import net.momirealms.customcrops.mechanic.misc.migrator.Migration;
import net.momirealms.customcrops.mechanic.requirement.RequirementManagerImpl;
import net.momirealms.customcrops.mechanic.world.WorldManagerImpl;
import net.momirealms.customcrops.scheduler.SchedulerImpl;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CustomCropsPluginImpl extends CustomCropsPlugin {

    private DependencyManager dependencyManager;
    private PacketManager packetManager;
    private CommandManager commandManager;
    private HologramManager hologramManager;

    @Override
    public void onLoad() {
        this.versionManager = new VersionManagerImpl(this);
        this.dependencyManager = new DependencyManagerImpl(this, new ReflectionClassPathAppender(this.getClassLoader()));
        this.dependencyManager.loadDependencies(new ArrayList<>(
                List.of(
                        Dependency.GSON,
                        Dependency.SLF4J_API,
                        Dependency.SLF4J_SIMPLE,
                        versionManager.isMojmap() ? Dependency.COMMAND_API_MOJMAP : Dependency.COMMAND_API,
                        Dependency.BOOSTED_YAML,
                        Dependency.BSTATS_BASE,
                        Dependency.BSTATS_BUKKIT
                )
        ));
    }

    @Override
    public void onEnable() {
        instance = this;
        this.adventure = new AdventureManagerImpl(this);
        this.scheduler = new SchedulerImpl(this);
        this.configManager = new ConfigManagerImpl(this);
        this.integrationManager = new IntegrationManagerImpl(this);
        this.conditionManager = new ConditionManagerImpl(this);
        this.actionManager = new ActionManagerImpl(this);
        this.requirementManager = new RequirementManagerImpl(this);
        this.coolDownManager = new CoolDownManager(this);
        this.worldManager = new WorldManagerImpl(this);
        this.itemManager = new ItemManagerImpl(this,
                AntiGriefLib.builder(this)
                .silentLogs(true)
                .ignoreOP(true)
                .build()
        );
        this.messageManager = new MessageManagerImpl(this);
        this.packetManager = new PacketManager(this);
        this.commandManager = new CommandManager(this);
        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.hologramManager = new HologramManager(this);
        this.commandManager.init();
        this.integrationManager.init();
        BukkitItemFactory.create(this);
        Migration.tryUpdating();
        this.reload();
        if (ConfigManager.metrics()) new Metrics(this, 16593);
        if (ConfigManager.checkUpdate()) {
            this.versionManager.checkUpdate().thenAccept(result -> {
                if (!result) this.getAdventure().sendConsoleMessage("[CustomCrops] You are using the latest version.");
                else this.getAdventure().sendConsoleMessage("[CustomCrops] Update is available: <u>https://polymart.org/resource/2625<!u>");
            });
        }
    }

    @Override
    public void onDisable() {
        if (this.commandManager != null) this.commandManager.disable();
        if (this.adventure != null) this.adventure.disable();
        if (this.requirementManager != null) this.requirementManager.disable();
        if (this.actionManager != null) this.actionManager.disable();
        if (this.worldManager != null) this.worldManager.disable();
        if (this.itemManager != null) this.itemManager.disable();
        if (this.conditionManager != null) this.conditionManager.disable();
        if (this.coolDownManager != null) this.coolDownManager.disable();
        if (this.placeholderManager != null) this.placeholderManager.disable();
        if (this.scheduler != null) ((SchedulerImpl) scheduler).shutdown();
        instance = null;
    }

    @Override
    public void reload() {
        this.configManager.reload();
        this.messageManager.reload();
        this.itemManager.reload();
        this.worldManager.reload();
        this.actionManager.reload();
        this.requirementManager.reload();
        this.conditionManager.reload();
        this.coolDownManager.reload();
        this.placeholderManager.reload();
        this.hologramManager.reload();
        ((SchedulerImpl) scheduler).reload();
        EventUtils.fireAndForget(new CustomCropsReloadEvent(this));
    }

    @Override
    public void debug(String debug) {
        if (ConfigManager.debug()) {
            LogUtils.info(debug);
        }
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    @Override
    public boolean isHookedPluginEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    @Override
    public boolean doesHookedPluginExist(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    @Override
    public String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion().split("-")[0];
    }
}
