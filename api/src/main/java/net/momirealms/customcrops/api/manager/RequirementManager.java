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

import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.RequirementFactory;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RequirementManager {

    /**
     * Registers a custom requirement type with its corresponding factory.
     *
     * @param type               The type identifier of the requirement.
     * @param requirementFactory The factory responsible for creating instances of the requirement.
     * @return True if registration was successful, false if the type is already registered.
     */
    boolean registerRequirement(String type, RequirementFactory requirementFactory);

    /**
     * Unregisters a custom requirement type.
     *
     * @param type The type identifier of the requirement to unregister.
     * @return True if unregistration was successful, false if the type is not registered.
     */
    boolean unregisterRequirement(String type);

    /**
     * Retrieves an array of requirements based on a configuration section.
     *
     * @param section The configuration section containing requirement definitions.
     * @param advanced A flag indicating whether to use advanced requirements.
     * @return An array of Requirement objects based on the configuration section
     */
    @Nullable Requirement[] getRequirements(ConfigurationSection section, boolean advanced);

    /**
     * Retrieves a Requirement object based on a configuration section and advanced flag.
     * <p>
     * requirement_1:  <- section
     *   type: xxx
     *   value: xxx
     *
     * @param section  The configuration section containing requirement definitions.
     * @param advanced A flag indicating whether to use advanced requirements.
     * @return A Requirement object based on the configuration section, or an EmptyRequirement if the section is null or invalid.
     */
    @NotNull Requirement getRequirement(ConfigurationSection section, boolean advanced);

    /**
     * Gets a requirement based on the provided type and value.
     * If a valid RequirementFactory is found for the type, it is used to create the requirement.
     * If no factory is found, a warning is logged, and an empty requirement instance is returned.
     * <p>
     * world:     <- type
     *   - world  <- value
     *
     * @param type   The type representing the requirement type.
     * @param value The value associated with the requirement.
     * @return A Requirement instance based on the type and value, or an EmptyRequirement if the type is invalid.
     */
    @NotNull Requirement getRequirement(String type, Object value);

    /**
     * Retrieves a RequirementFactory based on the specified requirement type.
     *
     * @param type The requirement type for which to retrieve a factory.
     * @return A RequirementFactory for the specified type, or null if no factory is found.
     */
    @Nullable RequirementFactory getRequirementFactory(String type);

    /**
     * Checks if an array of requirements is met for a given condition.
     *
     * @param condition    The Condition object to check against the requirements.
     * @param requirements An array of Requirement instances to be evaluated.
     * @return True if all requirements are met, false otherwise. Returns true if the requirements array is null.
     */
    static boolean isRequirementMet(State condition, Requirement... requirements) {
        if (requirements == null) return true;
        for (Requirement requirement : requirements) {
            if (!requirement.isStateMet(condition)) {
                return false;
            }
        }
        return true;
    }
}
