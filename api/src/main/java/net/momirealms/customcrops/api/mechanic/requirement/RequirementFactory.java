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

package net.momirealms.customcrops.api.mechanic.requirement;

import net.momirealms.customcrops.api.mechanic.action.Action;

import java.util.List;

/**
 * An interface for a requirement factory that builds requirements.
 */
public interface RequirementFactory {

    /**
     * Build a requirement with the given arguments, not met actions, and check action flag.
     *
     * @param args          The arguments used to build the requirement.
     * @param notMetActions Actions to be triggered when the requirement is not met (can be null).
     * @param advanced      Flag indicating whether to check the action when building the requirement.
     * @return The built requirement.
     */
    Requirement build(Object args, List<Action> notMetActions, boolean advanced);

    /**
     * Build a requirement with the given arguments.
     *
     * @param args The arguments used to build the requirement.
     * @return The built requirement.
     */
    default Requirement build(Object args) {
        return build(args, null, false);
    }
}
