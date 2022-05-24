package net.momirealms.customcrops;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        //指令注册
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(new CommandHandler());
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(new CommandTabComplete());
        Bukkit.getPluginManager().registerEvents(new RightClickCustomBlock(),this);
        Bukkit.getPluginManager().registerEvents(new BreakCustomBlock(),this);
        Bukkit.getPluginManager().registerEvents(new RightClickBlock(),this);
        Bukkit.getPluginManager().registerEvents(new BreakFurniture(),this);

        //修改末影箱标题
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.OPEN_WINDOW) {
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<WrappedChatComponent> components = packet.getChatComponents().getValues();
                for (WrappedChatComponent component : components) {
                    if(component.toString().contains("Ender Chest")){
                        component.setJson("{\"translate\":\"container.enderchest\"}");
                        packet.getChatComponents().write(components.indexOf(component), component);
                    }
                }
            }
        });

        //开始计时任务
        startTimer();

        //新建data文件
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

        //载入data数据
        FileConfiguration crop_data;
        FileConfiguration sprinkler_data;
        crop_data = YamlConfiguration.loadConfiguration(crop_file);
        sprinkler_data = YamlConfiguration.loadConfiguration(sprinkler_file);
        CustomCrops.cropManager = new CropManager(crop_data);
        CustomCrops.sprinklerManager = new SprinklerManager(sprinkler_data);

        //检测papi依赖
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders(this).register();
            MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] 检测到PlaceHolderAPI 已启用季节变量!",Bukkit.getConsoleSender());
        }
        //启动成功
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] 自定义农作物插件已启用！作者：小默米 QQ:3266959688",Bukkit.getConsoleSender());
        //this.getLogger().info("自定义农作物插件已启用！作者：小默米 QQ:3266959688");
    }

    @Override
    public void onDisable() {
        //关闭异步定时任务
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


    //定时任务
    public static void startTimer() {
        CustomCrops.timer = new CropTimer();
    }

    //重载插件
    public static void loadConfig(){
        instance.reloadConfig();
    }
}
