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

import net.momirealms.customcrops.config.MainConfig;
import org.bukkit.entity.Player;

public class ActionSkillXP implements ActionInterface {

    private final double xp;
    private final double chance;

    public ActionSkillXP(double xp, double chance) {
        this.xp = xp;
        this.chance = chance;
    }

    @Override
    public void performOn(Player player) {
        if (MainConfig.skillXP != null) {
            MainConfig.skillXP.addXp(player, xp);
        }
    }

    @Override
    public double getChance() {
        return chance;
    }
}
