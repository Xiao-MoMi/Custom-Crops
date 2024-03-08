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

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.momirealms.customcrops.api.integration.LevelInterface;
import org.bukkit.entity.Player;

public class MMOCoreImpl implements LevelInterface {

    @Override
    public void addXp(Player player, String target, double amount) {
        MMOCore.plugin.professionManager.get(target).giveExperience(PlayerData.get(player), amount, null ,EXPSource.OTHER);
    }

    @Override
    public int getLevel(Player player, String target) {
        return PlayerData.get(player).getCollectionSkills().getLevel(MMOCore.plugin.professionManager.get(target));
    }
}