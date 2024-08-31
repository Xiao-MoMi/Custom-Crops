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

package net.momirealms.customcrops.api.core.world;

import net.momirealms.customcrops.api.core.world.adaptor.WorldAdaptor;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface WorldManager extends Reloadable {

    Season getSeason(World world);

    int getDate(World world);

    CustomCropsWorld<?> loadWorld(World world);

    boolean unloadWorld(World world);

    Optional<CustomCropsWorld<?>> getWorld(World world);

    Optional<CustomCropsWorld<?>> getWorld(String world);

    boolean isWorldLoaded(World world);

    Set<WorldAdaptor<?>> adaptors();

    CustomCropsWorld<?> adapt(World world);

    CustomCropsWorld<?> adapt(String world);
}
