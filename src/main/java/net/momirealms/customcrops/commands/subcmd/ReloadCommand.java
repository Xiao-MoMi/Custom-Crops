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

package net.momirealms.customcrops.commands.subcmd;

import net.momirealms.customcrops.commands.AbstractSubCommand;
import net.momirealms.customcrops.commands.SubCommand;
import net.momirealms.customcrops.config.ConfigUtil;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            long time1 = System.currentTimeMillis();
            ConfigUtil.reloadConfigs();
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.reload.replace("{time}", String.valueOf(System.currentTimeMillis() - time1)));
            return true;
        }
        return super.onCommand(sender, args);
    }
}
