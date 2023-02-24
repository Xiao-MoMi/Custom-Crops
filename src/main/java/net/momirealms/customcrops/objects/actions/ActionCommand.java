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

package net.momirealms.customcrops.objects.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionCommand implements ActionInterface {

    private final String[] commands;
    private final double chance;

    public ActionCommand(String[] commands, double chance) {
        this.commands = commands;
        this.chance = chance;
    }

    @Override
    public void performOn(Player player) {
        for (String command : commands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
        }
    }

    @Override
    public double getChance() {
        return chance;
    }
}
