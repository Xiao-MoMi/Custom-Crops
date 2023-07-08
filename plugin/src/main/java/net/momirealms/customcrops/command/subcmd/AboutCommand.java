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
import net.momirealms.customcrops.command.AbstractSubCommand;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AboutCommand extends AbstractSubCommand {

    public static final AboutCommand INSTANCE = new AboutCommand();

    public AboutCommand() {
        super("about");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        AdventureUtils.sendMessage(sender, "<#FFA500>⛈ CustomCrops <gray>- <#F4A460>" + CustomCrops.getInstance().getVersionHelper().getPluginVersion());
        AdventureUtils.sendMessage(sender, "<#FFFFE0>Custom crop system based on thread pool");
        AdventureUtils.sendMessage(sender, "<#DA70D6>\uD83E\uDDEA Author: <#FFC0CB>XiaoMoMi");
        AdventureUtils.sendMessage(sender, "<#FF7F50>\uD83D\uDD25 Contributors: <#FFA07A>Cha_Shao<white>, <#FFA07A>TopOrigin<white>, <#FFA07A>AmazingCat");
        AdventureUtils.sendMessage(sender, "<#FFD700>⭐ <click:open_url:https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customcrops>Document</click> <#A9A9A9>| <#FAFAD2>⛏ <click:open_url:https://github.com/Xiao-MoMi/Custom-Crops>Github</click> <#A9A9A9>| <#48D1CC>\uD83D\uDD14 <click:open_url:https://polymart.org/resource/customcrops.2625>Polymart</click>");
        return true;
    }
}
