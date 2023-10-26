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

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customcrops.api.CustomCropsAPIImpl;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.api.object.crop.CropManager;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerManager;
import net.momirealms.customcrops.api.object.hologram.HologramManager;
import net.momirealms.customcrops.api.object.pot.PotManager;
import net.momirealms.customcrops.api.object.scheduler.Scheduler;
import net.momirealms.customcrops.api.object.season.SeasonManager;
import net.momirealms.customcrops.api.object.sprinkler.SprinklerManager;
import net.momirealms.customcrops.api.object.wateringcan.WateringCanManager;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.object.world.WorldDataManager;
import net.momirealms.customcrops.command.CustomCropsCommand;
import net.momirealms.customcrops.customplugin.Platform;
import net.momirealms.customcrops.customplugin.PlatformInterface;
import net.momirealms.customcrops.customplugin.PlatformManager;
import net.momirealms.customcrops.customplugin.itemsadder.ItemsAdderPluginImpl;
import net.momirealms.customcrops.customplugin.oraxen.OraxenPluginImpl;
import net.momirealms.customcrops.helper.LibraryLoader;
import net.momirealms.customcrops.helper.VersionHelper;
import net.momirealms.customcrops.integration.IntegrationManager;
import net.momirealms.customcrops.util.AdventureUtils;
import net.momirealms.protectionlib.ProtectionLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.util.TimeZone;

public final class CustomCrops extends CustomCropsPlugin {

    private static BukkitAudiences adventure;
    private static CustomCrops plugin;
    private static ProtocolManager protocolManager;
    private Platform platform;
    private PlatformInterface platformInterface;
    private CropManager cropManager;
    private IntegrationManager integrationManager;
    private WorldDataManager worldDataManager;
    private SprinklerManager sprinklerManager;
    private WateringCanManager wateringCanManager;
    private FertilizerManager fertilizerManager;
    private SeasonManager seasonManager;
    private PotManager potManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlatformManager platformManager;
    private HologramManager hologramManager;
    private VersionHelper versionHelper;
    private Scheduler scheduler;

    @Override
    public void onLoad(){
        plugin = this;
        instance = this;
        this.loadLibs();
        ProtectionLib.initialize(this);
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        this.versionHelper = new VersionHelper(this);
        if (versionHelper.isSpigot()) {
            AdventureUtils.consoleMessage("<red>========================[CustomCrops]=========================");
            AdventureUtils.consoleMessage("<red>       Spigot is not officially supported by CustomCrops");
            AdventureUtils.consoleMessage("<red>             Please use Paper or its forks");
            AdventureUtils.consoleMessage("<red>       Paper download link: https://papermc.io/downloads");
            AdventureUtils.consoleMessage("<red>==============================================================");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!this.loadPlatform()) return;
        this.registerCommands();
        AdventureUtils.consoleMessage("[CustomCrops] Running on <white>" + Bukkit.getVersion());
        ProtectionLib.hook();

        this.scheduler = new Scheduler(this);
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.cropManager = new CropManager(this);
        this.integrationManager = new IntegrationManager(this);
        this.seasonManager = new SeasonManager(this);
        this.worldDataManager = new WorldDataManager(this);
        this.sprinklerManager = new SprinklerManager(this);
        this.wateringCanManager = new WateringCanManager(this);
        this.fertilizerManager = new FertilizerManager(this);
        this.potManager = new PotManager(this);
        this.hologramManager = new HologramManager(this);
        this.platformManager = new PlatformManager(this);
        super.customCropsAPI = new CustomCropsAPIImpl(this);

        this.reload();

        for (World world : Bukkit.getWorlds()) {
            this.worldDataManager.loadWorld(world);
        }

        AdventureUtils.consoleMessage("[CustomCrops] Plugin Enabled!");
        if (ConfigManager.enableBStats) new Metrics(this, 16593);
        if (ConfigManager.checkUpdate) this.versionHelper.checkUpdate();
    }

    public void reload() {
        this.configManager.unload();
        this.messageManager.unload();
        this.cropManager.unload();
        this.integrationManager.unload();
        this.worldDataManager.unload();
        this.sprinklerManager.unload();
        this.wateringCanManager.unload();
        this.fertilizerManager.unload();
        this.potManager.unload();
        this.seasonManager.unload();
        this.platformManager.unload();
        this.hologramManager.unload();

        this.configManager.load();
        this.messageManager.load();
        this.integrationManager.load();
        this.cropManager.load();
        this.worldDataManager.load();
        this.sprinklerManager.load();
        this.wateringCanManager.load();
        this.fertilizerManager.load();
        this.potManager.load();
        this.seasonManager.load();
        this.platformManager.load();
        this.hologramManager.load();
    }

    @Override
    public void onDisable() {
        if (adventure != null) adventure.close();
        if (this.cropManager != null) this.cropManager.unload();
        if (this.worldDataManager != null) this.worldDataManager.disable();
        if (this.seasonManager != null) this.seasonManager.unload();
        if (this.sprinklerManager != null) this.sprinklerManager.unload();
        if (this.wateringCanManager != null) this.wateringCanManager.unload();
        if (this.fertilizerManager != null) this.fertilizerManager.unload();
        if (this.platformManager != null) this.platformManager.unload();
        if (this.potManager != null) this.potManager.unload();
        if (this.messageManager != null) this.messageManager.unload();
        if (this.configManager != null) this.configManager.unload();
        if (this.integrationManager != null) this.integrationManager.unload();
        if (this.hologramManager != null) this.hologramManager.unload();
        if (this.scheduler != null) this.scheduler.disable();
    }

    private void loadLibs() {
        TimeZone timeZone = TimeZone.getDefault();
        String libRepo = timeZone.getID().startsWith("Asia") ? "https://maven.aliyun.com/repository/public/" : "https://repo.maven.apache.org/maven2/";
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3.1", libRepo);
        LibraryLoader.load("commons-io","commons-io","2.11.0", libRepo);
        LibraryLoader.load("net.objecthunter","exp4j","0.4.8", libRepo);
    }

    private void registerCommands() {
        CustomCropsCommand customCropsCommand = new CustomCropsCommand();
        PluginCommand pluginCommand = Bukkit.getPluginCommand("customcrops");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(customCropsCommand);
            pluginCommand.setTabCompleter(customCropsCommand);
        }
    }

    private boolean loadPlatform() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("ItemsAdder") != null) {
            this.platform = Platform.ItemsAdder;
            this.platformInterface = new ItemsAdderPluginImpl();
        }
        else if (pluginManager.getPlugin("Oraxen") != null) {
            this.platform = Platform.Oraxen;
            this.platformInterface = new OraxenPluginImpl();
        }
        if (this.platform == null) {
            AdventureUtils.consoleMessage("<red>========================[CustomCrops]=========================");
            AdventureUtils.consoleMessage("<red>   Please install ItemsAdder or Oraxen as dependency.");
            AdventureUtils.consoleMessage("<red>  ItemsAdder Link: https://www.spigotmc.org/resources/73355/");
            AdventureUtils.consoleMessage("<red>   Oraxen link: https://www.spigotmc.org/resources/72448/");
            AdventureUtils.consoleMessage("<red>==============================================================");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        } else {
            AdventureUtils.consoleMessage("[CustomCrops] Platform: <green>" + platform.name());
            return true;
        }
    }

    public static BukkitAudiences getAdventure() {
        return adventure;
    }

    public static CustomCrops getInstance() {
        return plugin;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public CropManager getCropManager() {
        return cropManager;
    }

    public VersionHelper getVersionHelper() {
        return versionHelper;
    }

    public Platform getPlatform() {
        return platform;
    }

    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    public PlatformInterface getPlatformInterface() {
        return platformInterface;
    }

    public WorldDataManager getWorldDataManager() {
        return worldDataManager;
    }

    public SprinklerManager getSprinklerManager() {
        return sprinklerManager;
    }

    public PotManager getPotManager() {
        return potManager;
    }

    public WateringCanManager getWateringCanManager() {
        return wateringCanManager;
    }

    public FertilizerManager getFertilizerManager() {
        return fertilizerManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public PlatformManager getPlatformManager() {
        return platformManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
