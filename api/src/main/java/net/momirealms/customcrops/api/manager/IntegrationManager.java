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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Initable;
import net.momirealms.customcrops.api.integration.LevelInterface;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public interface IntegrationManager extends Initable {

    /**
     * Registers a level plugin with the specified name.
     *
     * @param plugin The name of the level plugin.
     * @param level The implementation of the LevelInterface.
     * @return true if the registration was successful, false if the plugin name is already registered.
     */
    boolean registerLevelPlugin(String plugin, LevelInterface level);

    /**
     * Unregisters a level plugin with the specified name.
     *
     * @param plugin The name of the level plugin to unregister.
     * @return true if the unregistration was successful, false if the plugin name is not found.
     */
    boolean unregisterLevelPlugin(String plugin);

    /**
     * Get the LevelInterface provided by a plugin.
     *
     * @param plugin The name of the plugin providing the LevelInterface.
     * @return The LevelInterface provided by the specified plugin, or null if the plugin is not registered.
     */
    @Nullable LevelInterface getLevelPlugin(String plugin);

    SeasonInterface getSeasonInterface();

    Season getSeason(World world);

    int getDate(World world);
}
