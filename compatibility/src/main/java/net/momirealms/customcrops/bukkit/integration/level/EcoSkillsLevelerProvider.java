/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.bukkit.integration.level;

import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.skills.Skills;
import net.momirealms.customcrops.api.integration.LevelerProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EcoSkillsLevelerProvider implements LevelerProvider {

    @Override
    public void addXp(@NotNull Player player, @NotNull String target, double amount) {
        EcoSkillsAPI.gainSkillXP(player, Objects.requireNonNull(Skills.INSTANCE.getByID(target)), amount);
    }

    @Override
    public int getLevel(@NotNull Player player, @NotNull String target) {
        return EcoSkillsAPI.getSkillLevel(player, Objects.requireNonNull(Skills.INSTANCE.getByID(target)));
    }

    @Override
    public String identifier() {
        return "EcoSkills";
    }
}
