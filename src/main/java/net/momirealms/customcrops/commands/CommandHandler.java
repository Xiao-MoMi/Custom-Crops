package net.momirealms.customcrops.commands;

import net.momirealms.customcrops.datamanager.ConfigManager;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.datamanager.MessageManager;
import net.momirealms.customcrops.datamanager.BackUp;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.NextSeason;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

public class CommandHandler implements CommandExecutor {

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) return true;
        //重载插件
        if(args[0].equalsIgnoreCase("reload")){

            ConfigManager.Config.ReloadConfig();

            if(sender instanceof Player){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.reload, (Player) sender);
            }else {
                MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.reload, Bukkit.getConsoleSender());
            }
            return true;
        }
        //设置季节
        if(args[0].equalsIgnoreCase("setseason")){
            if(args.length<2) return true;
            if(ConfigManager.Config.season){

                FileConfiguration config = CustomCrops.instance.getConfig();
                config.set("current-season", args[1]);
                CustomCrops.instance.saveConfig();
                ConfigManager.Config.current = args[1];

                if(sender instanceof Player){
                    MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.season_set.replace("{Season}",args[1])
                            .replace("spring", ConfigManager.Config.spring)
                            .replace("summer", ConfigManager.Config.summer)
                            .replace("autumn", ConfigManager.Config.autumn)
                            .replace("winter", ConfigManager.Config.winter), (Player) sender);
                }else {
                    MessageManager.consoleMessage(config.getString("messages.prefix") + ConfigManager.Config.season_set.replace("{Season}",args[1])
                            .replace("spring", ConfigManager.Config.spring)
                            .replace("summer", ConfigManager.Config.summer)
                            .replace("autumn", ConfigManager.Config.autumn)
                            .replace("winter", ConfigManager.Config.winter), Bukkit.getConsoleSender());
                }

            }else{
                if(sender instanceof Player){
                    MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.season_disabled, (Player) sender);
                }else {
                    MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.season_disabled, Bukkit.getConsoleSender());
                }
            }
            return true;
        }
        //强制保存
        if(args[0].equalsIgnoreCase("forcesave")){
            CropManager.saveData();
            SprinklerManager.saveData();
            if(sender instanceof Player){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.force_save, (Player) sender);
            }else {
                MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.force_save, Bukkit.getConsoleSender());
            }
            return true;
        }
        //强制生长
        if(args[0].equalsIgnoreCase("forcegrow")){
            if(args.length<2) return true;
            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.instance, () -> CropManager.CropGrow(args[1]));
            if(sender instanceof Player){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.force_grow, (Player) sender);
            }else {
                MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.force_grow, Bukkit.getConsoleSender());
            }
            return true;
        }
        //强制洒水
        if(args[0].equalsIgnoreCase("forcewater")){
            if(args.length<2) return true;
            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.instance, () -> SprinklerManager.SprinklerWork(args[1]));
            if(sender instanceof Player){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.force_water, (Player) sender);
            }else {
                MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.force_water, Bukkit.getConsoleSender());
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("backup")){
            BackUp.backUpData();
            if(sender instanceof Player){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.backup, (Player) sender);
            }else {
                MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.backup, Bukkit.getConsoleSender());
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("nextseason")){
            NextSeason.changeSeason();
            if(sender instanceof Player){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.nextSeason, (Player) sender);
            }else {
                MessageManager.consoleMessage(ConfigManager.Config.prefix + ConfigManager.Config.nextSeason, Bukkit.getConsoleSender());
            }
            return true;
        }
        return false;
    }
}
