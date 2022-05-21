package net.momirealms.customcrops;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageManager{

    public static void consoleMessage(String s, CommandSender sender) {
        BukkitAudiences bukkitAudiences = BukkitAudiences.create(CustomCrops.instance);
        Audience player = bukkitAudiences.sender(sender);
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        player.sendMessage(parsed);
    }
    public static void playerMessage(String s, Player player){
        BukkitAudiences bukkitAudiences = BukkitAudiences.create(CustomCrops.instance);
        Audience p = bukkitAudiences.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        p.sendMessage(parsed);
    }
}
