/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.command;

import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractSubCommand {

    private final String command;
    private Map<String, AbstractSubCommand> subCommandMap;

    public AbstractSubCommand(String command) {
        this.command = command;
    }

    public boolean onCommand(CommandSender sender, List<String> args) {
        if (subCommandMap == null || args.size() < 1) {
            return true;
        }
        AbstractSubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.unavailableArgs);
        } else {
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (subCommandMap == null)
            return Collections.singletonList("");
        if (args.size() <= 1) {
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args.get(0)));
            return returnList;
        }
        AbstractSubCommand subCmd = subCommandMap.get(args.get(0));
        if (subCmd != null)
            return subCommandMap.get(args.get(0)).onTabComplete(sender, args.subList(1, args.size()));
        return Collections.singletonList("");
    }

    public String getSubCommand() {
        return command;
    }

    public Map<String, AbstractSubCommand> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }

    public void regSubCommand(AbstractSubCommand command) {
        if (subCommandMap == null) {
            subCommandMap = new ConcurrentHashMap<>();
        }
        subCommandMap.put(command.getSubCommand(), command);
    }

    protected boolean noConsoleExecute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            AdventureUtils.consoleMessage(MessageManager.prefix + MessageManager.noConsole);
            return true;
        }
        return false;
    }

    protected boolean playerNotOnline(CommandSender commandSender, String player) {
        if (Bukkit.getPlayer(player) == null) {
            AdventureUtils.sendMessage(commandSender, MessageManager.prefix + MessageManager.notOnline.replace("{Player}", player));
            return true;
        }
        return false;
    }

    protected boolean lackArgs(CommandSender commandSender, int required, int current) {
        if (required > current) {
            AdventureUtils.sendMessage(commandSender, MessageManager.prefix + MessageManager.lackArgs);
            return true;
        }
        return false;
    }

    protected List<String> online_players() {
        List<String> online = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player -> online.add(player.getName())));
        return online;
    }

    protected List<String> filterStartingWith(List<String> list, String prefix) {
        return list.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
    }
}
