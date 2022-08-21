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

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customcrops.commands.Executor;
import net.momirealms.customcrops.commands.Completer;
import net.momirealms.customcrops.datamanager.*;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.helper.LibraryLoader;
import net.momirealms.customcrops.hook.Placeholders;
import net.momirealms.customcrops.listener.*;
import net.momirealms.customcrops.listener.itemframe.BreakBlockI;
import net.momirealms.customcrops.listener.itemframe.BreakFurnitureI;
import net.momirealms.customcrops.listener.itemframe.InteractFurnitureI;
import net.momirealms.customcrops.listener.itemframe.RightClickI;
import net.momirealms.customcrops.listener.tripwire.BreakBlockT;
import net.momirealms.customcrops.listener.tripwire.BreakFurnitureT;
import net.momirealms.customcrops.listener.tripwire.InteractFurnitureT;
import net.momirealms.customcrops.listener.tripwire.RightClickT;
import net.momirealms.customcrops.timer.CropTimer;
import net.momirealms.customcrops.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CustomCrops extends JavaPlugin {

    public static BukkitAudiences adventure;
    public static CustomCrops plugin;

    private CropTimer cropTimer;
    private CropManager cropManager;
    private SprinklerManager sprinklerManager;
    private SeasonManager seasonManager;
    private PotManager potManager;
    public static Placeholders placeholders;

    public CropManager getCropManager() { return this.cropManager; }
    public SprinklerManager getSprinklerManager() { return sprinklerManager; }
    public SeasonManager getSeasonManager() { return seasonManager; }
    public PotManager getPotManager() { return potManager; }

    @Override
    public void onLoad(){
        plugin = this;
        LibraryLoader.load("redis.clients","jedis","4.2.3","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("org.apache.commons","commons-pool2","2.11.1","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {

        adventure = BukkitAudiences.create(plugin);
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>Running on " + Bukkit.getVersion());

        ConfigReader.reloadConfig();
        if (!Objects.equals(ConfigReader.Config.version, "3")){
            ConfigUtil.update();
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null){
            placeholders = new Placeholders();
            placeholders.register();
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>PlaceHolderAPI <color:#FFEBCD>Hooked!");
            Bukkit.getPluginManager().registerEvents(new PapiReload(), this);
        }

        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(new Executor(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(new Completer());

        //公用事件
        Bukkit.getPluginManager().registerEvents(new ItemSpawn(), this);
        Bukkit.getPluginManager().registerEvents(new JoinAndQuit(), this);

        ConfigReader.tryEnableJedis();
        if (ConfigReader.Season.enable){
            this.seasonManager = new SeasonManager();
            this.seasonManager.loadData();
        }

        this.sprinklerManager = new SprinklerManager();
        this.sprinklerManager.loadData();
        this.potManager = new PotManager();
        this.potManager.loadData();
        this.cropTimer = new CropTimer();
        if (ConfigReader.Config.cropMode.equalsIgnoreCase("item_frame")){
            this.cropManager = new CropManager(true);
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>Crop Mode: ItemFrame");
            Bukkit.getPluginManager().registerEvents(new RightClickI(), this);
            Bukkit.getPluginManager().registerEvents(new BreakBlockI(), this);
            Bukkit.getPluginManager().registerEvents(new BreakFurnitureI(), this);
            Bukkit.getPluginManager().registerEvents(new InteractFurnitureI(), this);
        }else{
            this.cropManager = new CropManager(false);
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>Crop Mode: TripWire");
            Bukkit.getPluginManager().registerEvents(new RightClickT(), this);
            Bukkit.getPluginManager().registerEvents(new BreakBlockT(), this);
            Bukkit.getPluginManager().registerEvents(new BreakFurnitureT(), this);
            Bukkit.getPluginManager().registerEvents(new InteractFurnitureT(), this);
            checkIAConfig();
        }
        this.cropManager.loadData();
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        if (this.cropManager != null){
            this.cropManager.cleanData();
            this.cropManager.updateData();
            this.cropManager.saveData();
            this.cropManager = null;
        }
        if (this.sprinklerManager != null){
            this.sprinklerManager.cleanData();
            this.sprinklerManager.updateData();
            this.sprinklerManager.saveData();
            this.sprinklerManager = null;
        }
        if (this.potManager != null){
            this.potManager.saveData();
            this.potManager = null;
        }
        if (ConfigReader.Season.enable && !ConfigReader.Season.seasonChange && this.seasonManager != null){
            this.seasonManager.saveData();
            this.seasonManager = null;
        }
        if (placeholders != null){
            placeholders.unregister();
            placeholders = null;
        }

        getLogger().info("Backing Up...");
        FileUtil.backUpData();
        getLogger().info("Done.");

        if (cropTimer != null) {
            this.cropTimer.stopTimer(cropTimer.getTaskID());
        }
        if (adventure != null) {
            adventure.close();
        }
        if (plugin != null) {
            plugin = null;
        }
    }

    private void checkIAConfig(){
        FileConfiguration fileConfiguration = Bukkit.getPluginManager().getPlugin("ItemsAdder").getConfig();
        if (fileConfiguration.getBoolean("blocks.disable-REAL_WIRE")){
            fileConfiguration.set("blocks.disable-REAL_WIRE", false);
            try {
                fileConfiguration.save(new File(Bukkit.getPluginManager().getPlugin("ItemsAdder").getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><red>Detected that you might have not set \"disable-REAL_WIRE\" false in ItemsAdder's config!");
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><red>You need a restart to apply that config :)");
        }
    }
}
