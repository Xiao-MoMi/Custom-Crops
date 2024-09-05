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

package net.momirealms.customcrops.api.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The RequirementManager interface manages custom requirement types and provides methods for handling requirements.
 *
 * @param <T> the type of the context in which the requirements are evaluated.
 */
public interface RequirementManager<T> extends Reloadable {

    /**
     * Registers a custom requirement type with its corresponding factory.
     *
     * @param requirementFactory The factory responsible for creating instances of the requirement.
     * @param alias               The type identifier of the requirement.
     * @return True if registration was successful, false if the type is already registered.
     */
    boolean registerRequirement(@NotNull RequirementFactory<T> requirementFactory, @NotNull String... alias);

    /**
     * Unregisters a custom requirement type.
     *
     * @param type The type identifier of the requirement to unregister.
     * @return True if unregistration was successful, false if the type is not registered.
     */
    boolean unregisterRequirement(@NotNull String type);

    /**
     * Checks if a requirement type is registered.
     *
     * @param type The type identifier of the requirement.
     * @return True if the requirement type is registered, otherwise false.
     */
    boolean hasRequirement(@NotNull String type);

    /**
     * Retrieves a RequirementFactory based on the specified requirement type.
     *
     * @param type The requirement type for which to retrieve a factory.
     * @return A RequirementFactory for the specified type, or null if no factory is found.
     */
    @Nullable
    RequirementFactory<T> getRequirementFactory(@NotNull String type);

    /**
     * Retrieves an array of requirements based on a configuration section.
     *
     * @param section     The configuration section containing requirement definitions.
     * @param runActions  A flag indicating whether to use advanced requirements.
     * @return An array of Requirement objects based on the configuration section.
     */
    @NotNull
    Requirement<T>[] parseRequirements(Section section, boolean runActions);

    /**
     * Retrieves a Requirement object based on a configuration section and advanced flag.
     *
     * @param section     The configuration section containing requirement definitions.
     * @param runActions  A flag indicating whether to use advanced requirements.
     * @return A Requirement object based on the configuration section, or an EmptyRequirement if the section is null or invalid.
     */
    @NotNull
    Requirement<T> parseRequirement(@NotNull Section section, boolean runActions);

    /**
     * Gets a requirement based on the provided type and value.
     * If a valid RequirementFactory is found for the type, it is used to create the requirement.
     *
     * @param type  The type representing the requirement type.
     * @param value The value associated with the requirement.
     * @return A Requirement instance based on the type and value, or an EmptyRequirement if the type is invalid.
     */
    @NotNull
    Requirement<T> parseRequirement(@NotNull String type, @NotNull Object value);

    /**
     * Checks if all requirements in the provided array are satisfied within the given context.
     *
     * @param context      The context in which the requirements are evaluated.
     * @param requirements An array of requirements to check.
     * @return True if all requirements are satisfied, otherwise false.
     */
    static <T> boolean isSatisfied(Context<T> context, @Nullable Requirement<T>[] requirements) {
        if (requirements == null) return true;
        for (Requirement<T> requirement : requirements) {
            if (requirement == null) continue;
            if (!requirement.isSatisfied(context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all requirements in the provided array are satisfied within the given context.
     *
     * @param context      The context in which the requirements are evaluated.
     * @param requirements A list of requirements to check.
     * @return True if all requirements are satisfied, otherwise false.
     */
    static <T> boolean isSatisfied(Context<T> context, @Nullable List<Requirement<T>> requirements) {
        if (requirements == null) return true;
        for (Requirement<T> requirement : requirements) {
            if (!requirement.isSatisfied(context)) {
                return false;
            }
        }
        return true;
    }
}
