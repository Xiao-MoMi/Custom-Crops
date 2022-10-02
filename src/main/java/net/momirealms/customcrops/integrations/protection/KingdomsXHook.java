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

package net.momirealms.customcrops.integrations.protection;

import net.momirealms.customcrops.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.KingdomPlayer;

public class KingdomsXHook implements AntiGrief {

    @Override
    public boolean canBreak(Location location, Player player) {
        return kingdomsCheck(location, player);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return kingdomsCheck(location, player);
    }

    private boolean kingdomsCheck(Location location, Player player) {
        Land land = Land.getLand(location);
        if (land == null) return true;
        if (land.isClaimed()) {
            KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(player);
            Kingdom cropKingdom = land.getKingdom();
            if (kp.getKingdom() != null) {
                Kingdom kingdom = kp.getKingdom();
                return kingdom != cropKingdom;
            }
            else return false;
        }
        else return true;
    }
}
