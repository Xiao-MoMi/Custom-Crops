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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Completer implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender.isOp() || sender.hasPermission("customcrops.admin"))){
            return null;
        }
        if (args.length == 1) {
            List<String> arrayList = new ArrayList<>();
            for (String cmd : Arrays.asList("backup", "forcegrow", "forcesave", "forcewater", "reload", "setseason")) {
                if (cmd.startsWith(args[0]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("setseason") && args.length == 2){
            List<String> arrayList = new ArrayList<>();
            for (String cmd : ConfigReader.Config.worldNames) {
                if (cmd.startsWith(args[1]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("forcesave") && args.length == 2){
            List<String> arrayList = new ArrayList<>();
            if (ConfigReader.Season.enable){
                if (ConfigReader.Season.seasonChange){
                    for (String cmd : Arrays.asList("all","crop","pot","sprinkler")) {
                        if (cmd.startsWith(args[1]))
                            arrayList.add(cmd);
                    }
                }else{
                    for (String cmd : Arrays.asList("all","crop","pot","season","sprinkler")) {
                        if (cmd.startsWith(args[1]))
                            arrayList.add(cmd);
                    }
                }
            }else {
                for (String cmd : Arrays.asList("all","crop","pot","sprinkler")) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("setseason") && args.length == 3){
            List<String> arrayList = new ArrayList<>();
            for (String cmd : Arrays.asList("spring","summer","autumn","winter")) {
                if (cmd.startsWith(args[2]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(args[0].equalsIgnoreCase("forcegrow") || args[0].equalsIgnoreCase("forcewater")){
            List<String> arrayList = new ArrayList<>();
            for (String cmd : ConfigReader.Config.worldNames) {
                if (cmd.startsWith(args[1]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        return null;
    }
}
