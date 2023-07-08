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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMainCommand implements TabExecutor {

    protected final Map<String, AbstractSubCommand> subCommandMap;

    public AbstractMainCommand() {
        this.subCommandMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() < 1) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.nonArgs);
            return true;
        }
        if (!sender.hasPermission("customcrops." + argList.get(0))) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }
        AbstractSubCommand subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null)
            return subCommand.onCommand(sender, argList.subList(1, argList.size()));
        else {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.unavailableArgs);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() <= 1) {
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args[0]) || !sender.hasPermission("customcrops." + str));
            return returnList;
        }
        AbstractSubCommand subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null)
            return subCommand.onTabComplete(sender, argList.subList(1, argList.size()));
        else
            return Collections.singletonList("");
    }

    public void regSubCommand(AbstractSubCommand executor) {
        subCommandMap.put(executor.getSubCommand(), executor);
    }

    public Map<String, AbstractSubCommand> getSubCommandMap() {
        return subCommandMap;
    }
}
