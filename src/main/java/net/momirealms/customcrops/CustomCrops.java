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
import net.momirealms.customcrops.helper.LibraryLoader;
import net.momirealms.customcrops.listener.BreakBlock;
import net.momirealms.customcrops.listener.InteractEntity;
import net.momirealms.customcrops.listener.ItemSpawn;
import net.momirealms.customcrops.listener.RightClick;
import net.momirealms.customcrops.timer.CropTimer;
import net.momirealms.customcrops.timer.CropTimerAsync;
import net.momirealms.customcrops.utils.AdventureManager;
import net.momirealms.customcrops.utils.BackUp;
import net.momirealms.customcrops.utils.HoloUtil;
import net.momirealms.customcrops.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CustomCrops extends JavaPlugin {

    public static JavaPlugin instance;
    public static BukkitAudiences adventure;
    private CropTimer cropTimer;
    private CropTimerAsync cropTimerAsync;
    private CropManager cropManager;
    private SprinklerManager sprinklerManager;
    private SeasonManager seasonManager;
    private PotManager potManager;

    @Override
    public void onLoad(){

//        instance = this;
//        LibraryLoader.load("net.kyori","adventure-api","4.11.0","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-platform-api","4.1.1","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-platform-bukkit","4.1.1","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-platform-facet","4.1.1","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-text-serializer-gson","4.11.0","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-text-serializer-plain","4.11.0","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-text-serializer-gson-legacy-impl","4.11.0","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-nbt","4.11.0","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-key","4.11.0","https://oss.sonatype.org/content/groups/public");
//        LibraryLoader.load("net.kyori","adventure-text-minimessage","4.11.0","https://oss.sonatype.org/content/groups/public");

    }

    @Override
    public void onEnable() {

        instance = this;
        adventure = BukkitAudiences.create(instance);

        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>Running on " + Bukkit.getVersion());

        //加载配置文件
        ConfigReader.ReloadConfig();

        //PAPI
        if(Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null){
            new Placeholders().register();
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><gold>PlaceHolderAPI <color:#FFEBCD>Hooked!");
        }

        //指令注册
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(new Executor(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(new Completer());

        //注册事件
        Bukkit.getPluginManager().registerEvents(new ItemSpawn(), this);
        Bukkit.getPluginManager().registerEvents(new RightClick(), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlock(), this);
        Bukkit.getPluginManager().registerEvents(new InteractEntity(this), this);

        //开始计时器
        if(ConfigReader.Config.asyncCheck){
            this.cropTimerAsync = new CropTimerAsync(this);
        }else {
            this.cropTimer = new CropTimer(this);
        }

        //载入数据
        if (ConfigReader.Season.enable){
            this.seasonManager = new SeasonManager(this);
            this.seasonManager.loadData();
        }
        this.cropManager = new CropManager(this);
        this.cropManager.loadData();
        this.sprinklerManager = new SprinklerManager(this);
        this.sprinklerManager.loadData();
        this.potManager = new PotManager(this);
        this.potManager.loadData();

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
        }else {
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>Plugin Enabled!");
        }
    }

    @Override
    public void onDisable() {

        //保存数据
        this.cropManager.cleanData();
        this.cropManager.saveData();
        this.sprinklerManager.cleanData();
        this.sprinklerManager.saveData();
        this.potManager.saveData();
        if (ConfigReader.Season.enable && !ConfigReader.Season.seasonChange){
            this.seasonManager.saveData();
        }

        //备份数据
        getLogger().info("Back Up...");
        BackUp.backUpData();
        getLogger().info("Done.");

        //清除悬浮展示实体
        HoloUtil.cache.keySet().forEach(location -> {
            HoloUtil.cache.get(location).remove();
        });

        //关闭计时器
        if (cropTimer != null) {
            this.cropTimer.stopTimer(cropTimer.getTaskID());
        }
        if (cropTimerAsync != null){
            this.cropTimerAsync.stopTimer(cropTimerAsync.getTaskID());
        }

        if (adventure != null) {
            adventure.close();
        }

        instance = null;
    }

    public CropManager getCropManager() { return this.cropManager; }
    public SprinklerManager getSprinklerManager() { return sprinklerManager; }
    public SeasonManager getSeasonManager() { return seasonManager; }
    public PotManager getPotManager() { return potManager; }
}
