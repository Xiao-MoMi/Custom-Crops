package net.momirealms.customcrops;

import net.momirealms.customcrops.Libs.minedown.MineDown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageManager {
    public static void consoleMessage(String s, CommandSender sender) {
        sender.spigot().sendMessage(MineDown.parse(s));
    }
    public static void playerMessage(String s, Player player){
        player.spigot().sendMessage(MineDown.parse(s));
    }
}
