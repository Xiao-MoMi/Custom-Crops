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

import net.momirealms.customcrops.api.utils.CCSeason;
import net.momirealms.customcrops.api.utils.SeasonUtils;
import net.momirealms.customcrops.commands.AbstractSubCommand;
import net.momirealms.customcrops.commands.SubCommand;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.config.SeasonConfig;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetSeasonCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new SetSeasonCommand();

    public SetSeasonCommand() {
        super("setseason", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!SeasonConfig.enable) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.seasonDisabled);
            return true;
        }
        if (args.size() < 2) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.lackArgs);
            return true;
        }
        else {
            World world = Bukkit.getWorld(args.get(0));
            if (world == null) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.worldNotExists);
                return true;
            }
            CCSeason ccSeason;
            try {
                ccSeason = CCSeason.valueOf(args.get(1).toUpperCase());
            }
            catch (IllegalArgumentException e) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.seasonNotExists.replace("{season}", args.get(1)));
                return true;
            }
            SeasonUtils.setSeason(world, ccSeason);
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.setSeason.replace("{world}", args.get(0)).replace("{season}", args.get(1)));
        }
        return super.onCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (!SeasonConfig.enable) return null;
        if (args.size() == 1) {
            return getWorlds(args);
        }
        if (args.size() == 2) {
            List<String> seasons = List.of("Spring","Summer","Autumn","Winter");
            List<String> seasonList = new ArrayList<>();
            for (String season : seasons) {
                if (season.startsWith(args.get(1))) {
                    seasonList.add(season);
                }
            }
            return seasonList;
        }
        return super.onTabComplete(sender, args);
    }
}
