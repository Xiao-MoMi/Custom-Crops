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

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.api.object.season.SeasonData;
import net.momirealms.customcrops.command.AbstractSubCommand;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetDateCommand extends AbstractSubCommand {

    public static final SetDateCommand INSTANCE = new SetDateCommand();

    public SetDateCommand() {
        super("setdate");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (lackArgs(sender, 2, args.size())) return true;
        World world = Bukkit.getWorld(args.get(0));
        if (world == null) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.worldNotExist.replace("{world}", args.get(0)));
            return true;
        }
        try {
            int date = Integer.parseInt(args.get(1));
            SeasonData seasonData = CustomCrops.getInstance().getSeasonManager().unloadSeasonData(args.get(0));
            if (seasonData == null) {
                AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.noSeason);
                return true;
            }
            seasonData.setDate(date);
            CustomCrops.getInstance().getSeasonManager().loadSeasonData(seasonData);
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.setDate.replace("{world}", args.get(0)).replace("{date}", String.valueOf(date)));
            return true;
        } catch (NumberFormatException e) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + "Wrong number format");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return super.filterStartingWith(Bukkit.getWorlds().stream().filter(world -> CustomCrops.getInstance().getWorldDataManager().isWorldAllowed(world)).map(WorldInfo::getName).collect(Collectors.toList()), args.get(0));
        } else if (args.size() == 2) {
            ArrayList<String> dates = new ArrayList<>();
            for (int i = 1; i <= ConfigManager.seasonInterval; i++) {
                dates.add(String.valueOf(i));
            }
            return super.filterStartingWith(dates, args.get(1));
        }
        return null;
    }
}
