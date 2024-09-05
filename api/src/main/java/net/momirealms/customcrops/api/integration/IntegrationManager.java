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

import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for managing integration providers.
 * This allows for the registration and retrieval of various types of providers
 * such as Leveler, Enchantment, and Season providers.
 */
public interface IntegrationManager extends Reloadable {

    /**
     * Registers a LevelerProvider.
     *
     * @param levelerProvider the LevelerProvider to register
     * @return true if registration is successful, false otherwise
     */
    boolean registerLevelerProvider(@NotNull LevelerProvider levelerProvider);

    /**
     * Unregisters a LevelerProvider by its ID.
     *
     * @param id the ID of the LevelerProvider to unregister
     * @return true if unregistration is successful, false otherwise
     */
    boolean unregisterLevelerProvider(@NotNull String id);

    /**
     * Registers a SeasonProvider.
     *
     * @param seasonProvider the SeasonProvider to register
     */
    void registerSeasonProvider(@NotNull SeasonProvider seasonProvider);

    /**
     * Retrieves a registered LevelerProvider by its ID.
     *
     * @param id the ID of the LevelerProvider to retrieve
     * @return the LevelerProvider if found, or null if not found
     */
    @Nullable
    LevelerProvider getLevelerProvider(String id);

    /**
     * Registers an ItemProvider.
     *
     * @param itemProvider the ItemProvider to register
     * @return true if registration is successful, false otherwise.
     */
    boolean registerItemProvider(@NotNull ItemProvider itemProvider);

    /**
     * Unregisters an ItemProvider by its ID.
     *
     * @param id the ID of the ItemProvider to unregister
     * @return true if unregistration is successful, false otherwise.
     */
    boolean unregisterItemProvider(@NotNull String id);

    /**
     * Registers an EntityProvider.
     *
     * @param entityProvider the EntityProvider to register
     * @return true if registration is successful, false otherwise.
     */
    boolean registerEntityProvider(@NotNull EntityProvider entityProvider);

    /**
     * Unregisters an EntityProvider by its ID.
     *
     * @param id the ID of the EntityProvider to unregister
     * @return true if unregistration is successful, false otherwise.
     */
    boolean unregisterEntityProvider(@NotNull String id);

    /**
     * Retrieves a registered EntityProvider by its ID.
     *
     * @param id the ID of the EntityProvider to retrieve
     * @return the EntityProvider if found, or null if not found
     */
    @Nullable
    EntityProvider getEntityProvider(String id);
}
