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

package net.momirealms.customcrops.integration.skill;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.momirealms.customcrops.integration.SkillInterface;
import org.bukkit.entity.Player;

public class mcMMOImpl implements SkillInterface {

    @Override
    public void addXp(Player player, double amount) {
        ExperienceAPI.addRawXP(player, "Herbalism", (float) amount, "UNKNOWN");
    }

    @Override
    public int getLevel(Player player) {
        return ExperienceAPI.getLevel(player, PrimarySkillType.HERBALISM);
    }
}