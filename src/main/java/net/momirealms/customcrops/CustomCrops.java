package net.momirealms.customcrops;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customcrops.Crops.CropTimer;
import net.momirealms.customcrops.DataManager.BackUp;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import net.momirealms.customcrops.listener.BreakCustomBlock;
import net.momirealms.customcrops.listener.BreakFurniture;
import net.momirealms.customcrops.listener.RightClickBlock;
import net.momirealms.customcrops.listener.RightClickCustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class CustomCrops extends JavaPlugin {

    public static JavaPlugin instance;
    public static ProtocolManager manager;
    public static CropTimer timer;
    public static CropManager cropManager;
    public static SprinklerManager sprinklerManager;
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(new CommandHandler());
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(new CommandTabComplete());
        Bukkit.getPluginManager().registerEvents(new RightClickCustomBlock(),this);
        Bukkit.getPluginManager().registerEvents(new BreakCustomBlock(),this);
        Bukkit.getPluginManager().registerEvents(new RightClickBlock(),this);
        Bukkit.getPluginManager().registerEvents(new BreakFurniture(),this);

        startTimer();

        File crop_file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        File sprinkler_file = new File(CustomCrops.instance.getDataFolder(), "sprinkler-data.yml");
        if(!crop_file.exists()){
            try {
                crop_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!sprinkler_file.exists()){
            try {
                sprinkler_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration crop_data;
        FileConfiguration sprinkler_data;
        crop_data = YamlConfiguration.loadConfiguration(crop_file);
        sprinkler_data = YamlConfiguration.loadConfiguration(sprinkler_file);
        CustomCrops.cropManager = new CropManager(crop_data);
        CustomCrops.sprinklerManager = new SprinklerManager(sprinkler_data);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders(this).register();
            MessageManager.consoleMessage("<gradient:#ccfbff:#ef96c5>[CustomCorps] 检测到PlaceHolderAPI 已启用季节变量!</gradient>",Bukkit.getConsoleSender());
        }

        MessageManager.consoleMessage("<gradient:#ccfbff:#ef96c5>[CustomCorps] 自定义农作物插件已启用！作者：小默米</gradient>",Bukkit.getConsoleSender());
    }

    @Override
    public void onDisable() {
        if (CustomCrops.timer != null) {
            CropTimer.stopTimer(CustomCrops.timer.getTaskID());
        }

        CropManager.saveData();
        SprinklerManager.saveData();

        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        BackUp.backUpData();
        MessageManager.consoleMessage("<gradient:#ccfbff:#ef96c5>[CustomCorps] 自定义农作物插件已卸载！作者：小默米</gradient>",Bukkit.getConsoleSender());
    }

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public static void startTimer() {
        CustomCrops.timer = new CropTimer();
    }

    public static void loadConfig(){
        instance.reloadConfig();
    }
}
