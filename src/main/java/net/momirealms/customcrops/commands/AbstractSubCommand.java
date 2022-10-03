package net.momirealms.customcrops.commands;

import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSubCommand implements SubCommand {

    private final String command;
    private Map<String, SubCommand> subCommandMap;

    public AbstractSubCommand(String command, Map<String, SubCommand> subCommandMap) {
        this.command = command;
        this.subCommandMap = subCommandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (subCommandMap == null || args.size() < 1) {
            return true;
        }
        SubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            AdventureUtil.sendMessage(sender, MessageConfig.unavailableArgs);
        } else {
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (subCommandMap == null)
            return Collections.singletonList("");
        if (args.size() <= 1) {
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args.get(0)));
            return returnList;
        }
        SubCommand subCmd = subCommandMap.get(args.get(0));
        if (subCmd != null)
            return subCommandMap.get(args.get(0)).onTabComplete(sender, args.subList(1, args.size()));
        return Collections.singletonList("");
    }

    @Override
    public String getSubCommand() {
        return command;
    }

    @Override
    public Map<String, SubCommand> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }

    @Override
    public void regSubCommand(SubCommand command) {
        if (subCommandMap == null) {
            subCommandMap = new ConcurrentHashMap<>();
        }
        subCommandMap.put(command.getSubCommand(), command);
    }

    public List<String> getWorlds(List<String> args) {
        List<World> worlds = MainConfig.getWorldsList();
        List<String> worldNames = new ArrayList<>();
        for (World world : worlds) {
            if (world.getName().startsWith(args.get(0))) {
                worldNames.add(world.getName());
            }
        }
        return worldNames;
    }

    public void setSubCommandMap(Map<String, SubCommand> subCommandMap) {
        this.subCommandMap = subCommandMap;
    }
}
