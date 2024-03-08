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

package net.momirealms.customcrops.api.integration;

import org.bukkit.entity.Player;

public interface LevelInterface {

    /**
     * Add exp to a certain skill or job
     *
     * @param player player
     * @param target the skill or job, for instance "Fishing" "fisherman"
     * @param amount the exp amount
     */
    void addXp(Player player, String target, double amount);

    /**
     * Get a player's skill or job's level
     *
     * @param player player
     * @param target the skill or job, for instance "Fishing" "fisherman"
     * @return level
     */
    int getLevel(Player player, String target);
}
