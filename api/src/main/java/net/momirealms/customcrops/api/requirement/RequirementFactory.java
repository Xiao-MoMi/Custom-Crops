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

import net.momirealms.customcrops.api.action.Action;

import java.util.List;

/**
 * Interface representing a factory for creating requirements.
 *
 * @param <T> the type of object that the requirement will operate on
 */
public interface RequirementFactory<T> {

    /**
     * Build a requirement with the given arguments, not satisfied actions, and check run actions flag.
     *
     * @param args                The arguments used to build the requirement.
     * @param notSatisfiedActions Actions to be triggered when the requirement is not met (can be null).
     * @param runActions          Flag indicating whether to run the action if the requirement is not met.
     * @return The built requirement.
     */
    Requirement<T> process(Object args, List<Action<T>> notSatisfiedActions, boolean runActions);

    /**
     * Build a requirement with the given arguments.
     *
     * @param args The arguments used to build the requirement.
     * @return The built requirement.
     */
    default Requirement<T> process(Object args) {
        return process(args, List.of(), false);
    }
}
