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

package net.momirealms.customcrops.bukkit.integration.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.EmptyRequirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldGuardRegion {

    public static void register() {
        RequirementManager<Player> rm1 = BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class);
        RequirementManager<CustomCropsBlockState> rm2 = BukkitCustomCropsPlugin.getInstance().getRequirementManager(CustomCropsBlockState.class);
        for (RequirementManager<?> rm : List.of(rm1, rm2)) {
            rm.registerRequirement((args, notSatisfiedActions, runActions) -> {
                HashSet<String> regions = new HashSet<>();
                int mode = 1;
                if (args instanceof Section section) {
                    mode = section.getInt("mode", 1);
                    regions.addAll(section.getStringList("values"));
                } else {
                    if (args instanceof List<?> list) {
                        for (Object o : list) {
                            if (o instanceof String) {
                                regions.add((String) o);
                            }
                        }
                    } else {
                        BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at region requirement which is expected be `Section` or `StringList`");
                        return EmptyRequirement.instance();
                    }
                }
                int finalMode = mode;
                return context -> {
                    Location location = context.arg(ContextKeys.LOCATION);
                    if (location == null) return false;
                    RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
                    if (regionManager != null) {
                        ApplicableRegionSet set = regionManager.getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                        if (finalMode == 1) {
                            for (ProtectedRegion region : set) {
                                if (regions.contains(region.getId())) {
                                    return true;
                                }
                            }
                        } else if (finalMode == 2) {
                            outer: {
                                Set<String> ids = set.getRegions().stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
                                for (String region : regions) {
                                    if (!ids.contains(region)) {
                                        break outer;
                                    }
                                }
                                return true;
                            }
                        }
                    }
                    if (runActions) ActionManager.trigger(context, notSatisfiedActions);
                    return false;
                };
            }, "region");
        }
    }
}
