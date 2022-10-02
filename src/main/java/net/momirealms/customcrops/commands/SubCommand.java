package net.momirealms.customcrops.commands;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public interface SubCommand {

    boolean onCommand(CommandSender sender, List<String> args);

    List<String> onTabComplete(CommandSender sender, List<String> args);

    String getSubCommand();

    Map<String, SubCommand> getSubCommands();

    void regSubCommand(SubCommand subCommand);

}
