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

package net.momirealms.customcrops.integrations;

import net.momirealms.customcrops.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CCAntiGrief {

    String getName();

    boolean canBreak(Location location, Player player);

    boolean canPlace(Location location, Player player);

    static boolean testBreak(Player player, Location location) {
        if (player.isOp()) return true;
        for (CCAntiGrief CCAntiGrief : MainConfig.internalAntiGriefs) {
            if(!CCAntiGrief.canBreak(location, player)) {
                return false;
            }
        }
        for (CCAntiGrief CCAntiGrief : MainConfig.externalAntiGriefs) {
            if(!CCAntiGrief.canBreak(location, player)) {
                return false;
            }
        }
        return true;
    }

    static boolean testPlace(Player player, Location location) {
        if (player.isOp()) return true;
        for (CCAntiGrief CCAntiGrief : MainConfig.internalAntiGriefs) {
            if(!CCAntiGrief.canPlace(location, player)) {
                return false;
            }
        }
        for (CCAntiGrief CCAntiGrief : MainConfig.externalAntiGriefs) {
            if(!CCAntiGrief.canPlace(location, player)) {
                return false;
            }
        }
        return true;
    }
}
