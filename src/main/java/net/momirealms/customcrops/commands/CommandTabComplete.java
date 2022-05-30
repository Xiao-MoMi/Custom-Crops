package net.momirealms.customcrops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

public class CommandTabComplete implements TabCompleter {
    @Override
    @ParametersAreNonnullByDefault
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("backup" , "forcegrow", "forcesave", "forcewater", "reload", "setseason" , "nextseason");
        }
        if(args[0].equalsIgnoreCase("setseason")){
            return Arrays.asList("spring","summer","autumn","winter");
        }
        return null;
    }
}
