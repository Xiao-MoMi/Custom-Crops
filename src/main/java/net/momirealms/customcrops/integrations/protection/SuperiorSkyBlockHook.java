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

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import net.momirealms.customcrops.integrations.CCAntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SuperiorSkyBlockHook implements CCAntiGrief {

    @Override
    public String getName() {
        return "SuperiorSkyBlock";
    }

    @Override
    public boolean canBreak(Location location, Player player) {
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        if (island == null) return true;
        return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player), IslandPrivilege.getByName("BREAK"));
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        if (island == null) return true;
        return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player), IslandPrivilege.getByName("BUILD"));
    }
}
