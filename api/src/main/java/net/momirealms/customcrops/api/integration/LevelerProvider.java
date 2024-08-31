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

package net.momirealms.customcrops.api.integration;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The LevelerProvider interface defines methods to interact with external leveling
 * systems, allowing the management of experience points (XP) and levels for various
 * skills or jobs. Implementations of this interface should provide the logic for
 * adding XP to players and retrieving their levels in specific skills or jobs.
 */
public interface LevelerProvider extends ExternalProvider {

    /**
     * Add exp to a certain skill or job
     *
     * @param player player
     * @param target the skill or job, for instance "Fishing" "fisherman"
     * @param amount the exp amount
     */
    void addXp(@NotNull Player player, @NotNull String target, double amount);

    /**
     * Get a player's skill or job's level
     *
     * @param player player
     * @param target the skill or job, for instance "Fishing" "fisherman"
     * @return level
     */
    int getLevel(@NotNull Player player, @NotNull String target);
}
