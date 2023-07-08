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

package net.momirealms.customcrops.command.subcmd;

import net.momirealms.customcrops.command.AbstractSubCommand;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends AbstractSubCommand {

    public static final HelpCommand INSTANCE = new HelpCommand();

    public HelpCommand() {
        super("help");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        AdventureUtils.sendMessage(sender, "<#FFA500>Command usage:");
        AdventureUtils.sendMessage(sender, "  <gray>├─<#FFFACD><Required Augument> ");
        AdventureUtils.sendMessage(sender, "  <gray>└─<#FFFACD><#E1FFFF>[Optional Augument]");
        AdventureUtils.sendMessage(sender, "<#FFA500>/customcrops");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>help");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>about");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>reload <#87CEFA>Reload the plugin");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>migrate <#87CEFA>Migrate data from 2.0->3.0");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>convert <#87CEFA>Convert config from 2.0->3.0");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>setseason <#FFFACD><world> <season> <#87CEFA>Set a world's season");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>setdate <#FFFACD><world> <date> <#87CEFA>Set a world's date");
        AdventureUtils.sendMessage(sender, "  <gray>└─<white>force");
        AdventureUtils.sendMessage(sender, "  <gray>    ├─<white>sprinklerwork <#FFFACD><world> <#87CEFA>Forced sprinklers to work");
        AdventureUtils.sendMessage(sender, "  <gray>    └─<white>consume <#FFFACD><world> <#87CEFA>Forced pots to reduce water amount and the remaining use of fertilizers");
        return true;
    }
}
