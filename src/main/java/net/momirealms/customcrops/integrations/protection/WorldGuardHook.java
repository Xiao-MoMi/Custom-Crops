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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.momirealms.customcrops.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook implements AntiGrief {

    @Override
    public boolean canPlace(Location location, Player player) {
        if (player.isOp()) return true;
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = BukkitAdapter.adapt(location.getWorld());
        WorldGuardPlatform platform = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform();
        if (hasRegion(world, BukkitAdapter.asBlockVector(location))){
            RegionQuery query = platform.getRegionContainer().createQuery();
            return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BUILD);
        }
        else return true;
    }

    @Override
    public boolean canBreak(Location location, Player player) {
        if (player.isOp()) return true;
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = BukkitAdapter.adapt(location.getWorld());
        WorldGuardPlatform platform = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform();
        if (hasRegion(world, BukkitAdapter.asBlockVector(location))){
            RegionQuery query = platform.getRegionContainer().createQuery();
            return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK);
        }
        else return true;
    }

    private boolean hasRegion(World world, BlockVector3 vector){
        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(world);
        if (regionManager == null) return true;
        return regionManager.getApplicableRegions(vector).size() > 0;
    }
}