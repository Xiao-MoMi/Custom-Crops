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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The EntityProvider interface defines methods to interact with external entity
 * spawning systems, allowing the spawning of entities at specified locations with
 * given properties. Implementations of this interface should provide the logic
 * for spawning entities and managing their properties.
 */
public interface EntityProvider extends ExternalProvider {

    /**
     * Spawns an entity at the specified location with the given properties.
     *
     * @param location     The location where the entity will be spawned.
     * @param id           The identifier of the entity to be spawned.
     * @param propertyMap  A map containing additional properties for the entity.
     * @return The spawned entity.
     */
    @NotNull
    Entity spawn(@NotNull Location location, @NotNull String id, @NotNull Map<String, Object> propertyMap);

    default Entity spawn(@NotNull Location location, @NotNull String id) {
        return spawn(location, id, new HashMap<>());
    }
}
