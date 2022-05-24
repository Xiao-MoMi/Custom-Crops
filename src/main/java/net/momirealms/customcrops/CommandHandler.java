package net.momirealms.customcrops;

import net.momirealms.customcrops.DataManager.BackUp;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.NextSeason;
import net.momirealms.customcrops.DataManager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class CommandHandler implements CommandExecutor {


    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player && !sender.isOp()){
            return false;
        }

        FileConfiguration config = CustomCrops.instance.getConfig();

        //重载插件
        if(args[0].equalsIgnoreCase("reload")){
            CustomCrops.loadConfig();
            if(sender instanceof Player){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.reload"), (Player) sender);
            }else {
                MessageManager.consoleMessage(config.getString("messages.prefix") + config.getString("messages.reload"), Bukkit.getConsoleSender());
            }
        }
        //设置季节
        if(args[0].equalsIgnoreCase("setseason")){
            if(config.getBoolean("enable-season")){
                config.set("current-season", args[1]);
                if(sender instanceof Player){
                    MessageManager.playerMessage(config.getString("messages.prefix") + Objects.requireNonNull(config.getString("messages.season-set")).replace("{Season}",args[1])
                            .replace("spring", Objects.requireNonNull(config.getString("messages.spring")))
                            .replace("summer", Objects.requireNonNull(config.getString("messages.summer")))
                            .replace("autumn", Objects.requireNonNull(config.getString("messages.autumn")))
                            .replace("winter", Objects.requireNonNull(config.getString("messages.winter"))), (Player) sender);
                }else {
                    MessageManager.consoleMessage(config.getString("messages.prefix") + Objects.requireNonNull(config.getString("messages.season-set")).replace("{Season}",args[1])
                            .replace("spring", Objects.requireNonNull(config.getString("messages.spring")))
                            .replace("summer", Objects.requireNonNull(config.getString("messages.summer")))
                            .replace("autumn", Objects.requireNonNull(config.getString("messages.autumn")))
                            .replace("winter", Objects.requireNonNull(config.getString("messages.winter"))), Bukkit.getConsoleSender());
                }
                CustomCrops.instance.saveConfig();
            }else{
                if(sender instanceof Player){
                    MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.season-disabled"), (Player) sender);
                }else {
                    MessageManager.consoleMessage(config.getString("messages.prefix") + config.getString("messages.season-disabled"), Bukkit.getConsoleSender());
                }
            }
        }
        //强制保存
        if(args[0].equalsIgnoreCase("forcesave")){
            CropManager.saveData();
            SprinklerManager.saveData();
            if(sender instanceof Player){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.force-save"), (Player) sender);
            }else {
                MessageManager.consoleMessage(config.getString("messages.prefix") + config.getString("messages.force-save"), Bukkit.getConsoleSender());
            }
        }
        //清除缓存
        if(args[0].equalsIgnoreCase("cleancache")){
            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.instance,()->{
                CropManager.cleanLoadedCache();
                SprinklerManager.cleanCache();
            });
            if(sender instanceof Player){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.clean-cache"), (Player) sender);
            }else {
                MessageManager.consoleMessage(config.getString("messages.prefix") + config.getString("messages.clean-cache"), Bukkit.getConsoleSender());
            }
        }
        if(args[0].equalsIgnoreCase("backup")){
            BackUp.backUpData();
            if(sender instanceof Player){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.backup"), (Player) sender);
            }else {
                MessageManager.consoleMessage(config.getString("messages.prefix") + config.getString("messages.backup"), Bukkit.getConsoleSender());
            }
        }
        if(args[0].equalsIgnoreCase("nextseason")){
            NextSeason.changeSeason();
            if(sender instanceof Player){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.nextseason"), (Player) sender);
            }else {
                MessageManager.consoleMessage(config.getString("messages.prefix") + config.getString("messages.nextseason"), Bukkit.getConsoleSender());
            }
        }
        return false;
    }
}
