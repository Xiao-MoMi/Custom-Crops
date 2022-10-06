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

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.commands.AbstractSubCommand;
import net.momirealms.customcrops.commands.SubCommand;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SimulateCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new SimulateCommand();

    private SimulateCommand() {
        super("simulate", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 4) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.lackArgs);
        }
        else {
            World world = Bukkit.getWorld(args.get(0));
            if (world == null) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.worldNotExists);
                return true;
            }
            int sprinklerTime;
            int growTime;
            int dryTime;
            try {
                sprinklerTime = Integer.parseInt(args.get(1));
                growTime = Integer.parseInt(args.get(3));
                dryTime = Integer.parseInt(args.get(2));
                if (sprinklerTime <= 0 || growTime <= 0 || dryTime <= 0 || (sprinklerTime + growTime + dryTime) > 23999) {
                    AdventureUtil.sendMessage(sender, MessageConfig.prefix + "Time should be a positive number between 1-23999");
                    return true;
                }
            }
            catch (IllegalArgumentException e) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + "Time should be a positive number between 1-23999");
                e.printStackTrace();
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
                CustomCrops.plugin.getCropManager().grow(world, growTime, sprinklerTime, dryTime, false);
            });
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.growSimulation);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return getWorlds(args);
        }
        if (args.size() == 2) {
            return List.of("<SprinklerWorkTime>");
        }
        if (args.size() == 3) {
            return List.of("<PotDryTime>");
        }
        if (args.size() == 4) {
            return List.of("<CropGrowTime>");
        }
        return super.onTabComplete(sender, args);
    }
}
