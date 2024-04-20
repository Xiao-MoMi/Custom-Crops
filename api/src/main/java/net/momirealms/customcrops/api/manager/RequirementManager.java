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

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.RequirementFactory;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RequirementManager extends Reloadable {

    /**
     * Register a custom requirement type
     *
     * @param type type
     * @param requirementFactory requirement factory
     * @return success or not
     */
    boolean registerRequirement(String type, RequirementFactory requirementFactory);

    /**
     * Unregister a custom requirement by type
     *
     * @param type type
     * @return success or not
     */
    boolean unregisterRequirement(String type);

    /**
     * Build requirements with Bukkit configs
     *
     * @param section bukkit config
     * @param advanced check "not-met-actions" or not
     * @return requirements
     */
    @Nullable
    Requirement[] getRequirements(ConfigurationSection section, boolean advanced);

    /**
     * If a requirement type exists
     *
     * @param type type
     * @return exist or not
     */
    default boolean hasRequirement(String type) {
        return getRequirementFactory(type) != null;
    }

    /**
     * Build a requirement instance with Bukkit configs
     *
     * @param section bukkit config
     * @param advanced check "not-met-actions" or not
     * @return requirement
     */
    @NotNull
    Requirement getRequirement(ConfigurationSection section, boolean advanced);

    /**
     * Build a requirement instance with Bukkit configs
     *
     * @return requirement
     */
    @NotNull
    Requirement getRequirement(String type, Object value);

    /**
     * Get a requirement factory by type
     *
     * @param type type
     * @return requirement factory
     */
    @Nullable
    RequirementFactory getRequirementFactory(String type);

    /**
     * Are requirements met for a player
     *
     * @param state state
     * @param requirements requirements
     * @return meet or not
     */
    static boolean isRequirementMet(State state, Requirement... requirements) {
        if (requirements == null) return true;
        for (Requirement requirement : requirements) {
            if (!requirement.isStateMet(state)) {
                return false;
            }
        }
        return true;
    }
}
