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
import net.momirealms.customcrops.api.integration.SeasonProvider;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.World;

import java.util.Optional;
import java.util.TreeSet;

/**
 * WorldManager is responsible for managing the lifecycle and state of worlds in the CustomCrops plugin.
 * It provides methods to load, unload, and adapt worlds, manage seasonal changes, and interact with
 * different world adaptors.
 */
public interface WorldManager extends Reloadable {

    /**
     * Gets the current SeasonProvider used for determining the season of each world.
     *
     * @return The current SeasonProvider.
     */
    SeasonProvider seasonProvider();

    /**
     * Retrieves the current season for a given Bukkit world.
     *
     * @param world The Bukkit world to get the season for.
     * @return The current season of the specified world.
     */
    Season getSeason(World world);

    /**
     * Retrieves the current date for a given Bukkit world.
     *
     * @param world The Bukkit world to get the date for.
     * @return The current date of the specified world.
     */
    int getDate(World world);

    /**
     * Loads a CustomCrops world based on the specified Bukkit world.
     *
     * @param world The Bukkit world to load as a CustomCrops world.
     * @return The loaded CustomCropsWorld instance.
     */
    CustomCropsWorld<?> loadWorld(World world);

    /**
     * Unloads the CustomCrops world associated with the specified Bukkit world.
     *
     * @param world     The Bukkit world to unload.
     * @param disabling
     * @return True if the world was successfully unloaded, false otherwise.
     */
    boolean unloadWorld(World world, boolean disabling);

    /**
     * Checks if mechanism is enabled for a certain world
     *
     * @param world world
     * @return enabled or not
     */
    boolean isMechanicEnabled(World world);

    /**
     * Retrieves a CustomCrops world based on the specified Bukkit world, if loaded.
     *
     * @param world The Bukkit world to retrieve the CustomCrops world for.
     * @return An Optional containing the CustomCropsWorld instance if loaded, otherwise empty.
     */
    Optional<CustomCropsWorld<?>> getWorld(World world);

    /**
     * Retrieves a CustomCrops world based on the world name, if loaded.
     *
     * @param world The name of the world to retrieve.
     * @return An Optional containing the CustomCropsWorld instance if loaded, otherwise empty.
     */
    Optional<CustomCropsWorld<?>> getWorld(String world);

    /**
     * Checks if a given Bukkit world is currently loaded as a CustomCrops world.
     *
     * @param world The Bukkit world to check.
     * @return True if the world is loaded, false otherwise.
     */
    boolean isWorldLoaded(World world);

    /**
     * Retrieves all available world adaptors.
     *
     * @return A set of WorldAdaptor instances.
     */
    TreeSet<WorldAdaptor<?>> adaptors();

    /**
     * Adapts a Bukkit world into a CustomCrops world.
     *
     * @param world The Bukkit world to adapt.
     * @return The adapted CustomCropsWorld instance.
     */
    CustomCropsWorld<?> adapt(World world);

    /**
     * Adapts a world by its name into a CustomCrops world.
     *
     * @param world The name of the world to adapt.
     * @return The adapted CustomCropsWorld instance.
     */
    CustomCropsWorld<?> adapt(String world);
}