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

package net.momirealms.customcrops.compatibility.level;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.leveler.Leveler;
import net.momirealms.customcrops.api.integration.LevelInterface;
import org.bukkit.entity.Player;

public class AureliumSkillsImpl implements LevelInterface {

    private final Leveler leveler;

    public AureliumSkillsImpl() {
        leveler = AureliumAPI.getPlugin().getLeveler();
    }

    @Override
    public void addXp(Player player, String target, double amount) {
        leveler.addXp(player, AureliumAPI.getPlugin().getSkillRegistry().getSkill(target), amount);
    }

    @Override
    public int getLevel(Player player, String target) {
        return AureliumAPI.getSkillLevel(player, AureliumAPI.getPlugin().getSkillRegistry().getSkill(target));
    }
}
