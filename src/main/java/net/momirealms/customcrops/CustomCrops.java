package net.momirealms.customcrops;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customcrops.commands.Executor;
import net.momirealms.customcrops.commands.Completer;
import net.momirealms.customcrops.datamanager.*;
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
import org.bukkit.plugin.java.JavaPlugin;

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
    public void onEnable() {

        instance = this;
        adventure = BukkitAudiences.create(this);

        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#FFEBCD>Running on " + Bukkit.getVersion());

        //加载配置文件
        ConfigReader.ReloadConfig();

        //PAPI
        if(Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null){
            new Placeholders().register();
            AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>检测到 <gold>PlaceHolderAPI <color:#FFEBCD>已启用变量!");
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

        //启动完成
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>插件已加载！作者：小默米 QQ:3266959688");
    }

    @Override
    public void onDisable() {

        //保存数据
        this.cropManager.saveData();
        this.sprinklerManager.saveData();
        this.potManager.saveData();
        if (ConfigReader.Season.enable && !ConfigReader.Season.seasonChange){
            this.seasonManager.saveData();
        }

        //备份数据
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>插件数据自动备份中...");
        BackUp.backUpData();
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>备份已完成!");

        //清除悬浮展示实体
        HoloUtil.cache.keySet().forEach(player -> {
            HoloUtil.cache.get(player).remove();
        });

        //关闭计时器
        if (cropTimer != null) {
            this.cropTimer.stopTimer(cropTimer.getTaskID());
        }
        if (cropTimerAsync != null){
            this.cropTimerAsync.stopTimer(cropTimerAsync.getTaskID());
        }

        //卸载完成
        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] </gradient><color:#F5DEB3>插件已卸载！作者：小默米 QQ:3266959688");
    }

    public CropManager getCropManager() { return this.cropManager; }
    public SprinklerManager getSprinklerManager() { return sprinklerManager; }
    public SeasonManager getSeasonManager() { return seasonManager; }
    public PotManager getPotManager() { return potManager; }
}
