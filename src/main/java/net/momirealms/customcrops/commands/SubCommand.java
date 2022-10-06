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
