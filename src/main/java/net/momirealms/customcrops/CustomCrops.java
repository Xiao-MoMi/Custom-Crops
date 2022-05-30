package net.momirealms.customcrops;

import net.momirealms.customcrops.commands.CommandHandler;
import net.momirealms.customcrops.commands.CommandTabComplete;
import net.momirealms.customcrops.datamanager.*;
import net.momirealms.customcrops.listener.BreakCrops;
import net.momirealms.customcrops.timer.CropTimer;
import net.momirealms.customcrops.listener.BreakCustomBlock;
import net.momirealms.customcrops.listener.RightClickBlock;
import net.momirealms.customcrops.listener.RightClickCustomBlock;
import net.momirealms.customcrops.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CustomCrops extends JavaPlugin {

    public static JavaPlugin instance;
    public static CropTimer timer;
    public static CropManager cropManager;
    public static SprinklerManager sprinklerManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        //加载配置文件
        ConfigManager.Config.ReloadConfig();

        //指令注册
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(new CommandHandler());
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(new CommandTabComplete());
        Bukkit.getPluginManager().registerEvents(new RightClickCustomBlock(),this);
        Bukkit.getPluginManager().registerEvents(new BreakCustomBlock(),this);
        Bukkit.getPluginManager().registerEvents(new RightClickBlock(),this);
        Bukkit.getPluginManager().registerEvents(new BreakCrops(),this);

        //开始计时任务
        CustomCrops.timer = new CropTimer();

        //新建data文件
        File crop_file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        File sprinkler_file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        if(!crop_file.exists()){
            try {
                crop_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                MessageManager.consoleMessage("&c[CustomCrops] 农作物数据文件生成失败!",Bukkit.getConsoleSender());
            }
        }
        if(!sprinkler_file.exists()){
            try {
                sprinkler_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                MessageManager.consoleMessage("&c[CustomCrops] 洒水器数据文件生成失败!",Bukkit.getConsoleSender());
            }
        }

        //载入data数据
        CropManager.loadData();
        SprinklerManager.loadData();

        //检测papi依赖
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders(this).register();
            MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] 检测到PlaceHolderAPI 已启用季节变量!",Bukkit.getConsoleSender());
        }

        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] 自定义农作物插件已启用！作者：小默米 QQ:3266959688",Bukkit.getConsoleSender());

    }

    @Override
    public void onDisable() {
        //关闭定时任务
        if (CustomCrops.timer != null) {
            CropTimer.stopTimer(CustomCrops.timer.getTaskID());
        }

        //保存缓存中的数据
        CropManager.saveData();
        SprinklerManager.saveData();

        //备份
        BackUp.backUpData();
        MessageManager.consoleMessage(("&#ccfbff-#ef96c5&[CustomCrops] 自定义农作物插件已卸载！作者：小默米 QQ:3266959688"),Bukkit.getConsoleSender());
    }
}
