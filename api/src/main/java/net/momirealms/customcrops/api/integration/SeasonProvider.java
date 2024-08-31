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

import net.momirealms.customcrops.api.core.world.Season;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * The SeasonProvider interface defines methods to interact with external seasonal
 * systems, allowing the retrieval of the current season for a specific world.
 * Implementations of this interface should provide the logic for determining the
 * season based on the world context.
 */
public interface SeasonProvider extends ExternalProvider {

    /**
     * Get a world's season
     *
     * @param world world
     * @return spring, summer, autumn, winter or disabled
     */
    @NotNull
    Season getSeason(@NotNull World world);
}
