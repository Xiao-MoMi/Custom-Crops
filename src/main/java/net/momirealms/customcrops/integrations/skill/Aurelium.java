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

package net.momirealms.customcrops.integrations.skill;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.leveler.Leveler;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;

public class Aurelium implements SkillXP {

    private static final Leveler leveler = AureliumAPI.getPlugin().getLeveler();
    private static final Skill skill = AureliumAPI.getPlugin().getSkillRegistry().getSkill("farming");

    @Override
    public void addXp(Player player, double amount) {
        leveler.addXp(player, skill, amount);
    }
}
