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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

import java.util.Optional;

public class BentoBoxIntegration implements Integration{

    @Override
    public boolean canBreak(Location location, Player player) {
        User user = User.getInstance(player);
        Optional<Island> islandOptional = BentoBox.getInstance().getIslands().getIslandAt(location);
        return islandOptional.map(island -> island.isAllowed(user, Flags.BREAK_BLOCKS)).orElse(true);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        User user = User.getInstance(player);
        Optional<Island> islandOptional = BentoBox.getInstance().getIslands().getIslandAt(location);
        return islandOptional.map(island -> island.isAllowed(user, Flags.PLACE_BLOCKS)).orElse(true);
    }
}
