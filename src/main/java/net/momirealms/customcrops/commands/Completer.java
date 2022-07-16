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

import net.momirealms.customcrops.ConfigReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

public class Completer implements TabCompleter {

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender.isOp() || sender.hasPermission("customcrops.admin"))){
            return null;
        }
        if (args.length == 1) {
            return Arrays.asList("backup", "forcegrow", "forcesave", "forcewater", "reload", "setseason");
        }
        if(args[0].equalsIgnoreCase("setseason") && args.length == 2){
            return ConfigReader.Config.worldNames;
        }
        if(args[0].equalsIgnoreCase("forcesave") && args.length == 2){
            if (ConfigReader.Season.enable){
                if (ConfigReader.Season.seasonChange){
                    return Arrays.asList("all","crop","pot","sprinkler");
                }else{
                    return Arrays.asList("all","crop","pot","season","sprinkler");
                }
            }else {
                return Arrays.asList("all","crop","pot","sprinkler");
            }
        }
        if(args[0].equalsIgnoreCase("setseason") && args.length == 3){
            return Arrays.asList("spring","summer","autumn","winter");
        }
        if(args[0].equalsIgnoreCase("forcegrow") || args[0].equalsIgnoreCase("forcewater")){
            return ConfigReader.Config.worldNames;
        }
        return null;
    }
}
