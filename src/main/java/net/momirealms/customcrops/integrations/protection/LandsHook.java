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

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.types.RoleFlag;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.LandWorld;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LandsHook implements AntiGrief {

    private final LandsIntegration api;

    public LandsHook() {
        api = LandsIntegration.of(CustomCrops.plugin);;
    }

    @Override
    public boolean canBreak(Location location, Player player) {
        LandWorld world = api.getWorld(location.getWorld());
        if (world != null) {
            return world.hasRoleFlag(player.getUniqueId(), location, RoleFlag.of("BLOCK_BREAK"));
        }
        return true;
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        LandWorld world = api.getWorld(location.getWorld());
        if (world != null) {
            return world.hasRoleFlag(player.getUniqueId(), location, RoleFlag.of("BLOCK_PLACE"));
        }
        return true;
    }
}
