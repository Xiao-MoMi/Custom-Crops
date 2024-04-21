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

public interface RequirementFactory {

    /**
     * Build a requirement
     *
     * @param args args
     * @param notMetActions actions to perform if the requirement is not met
     * @param advanced whether to trigger the notMetActions or not
     * @return requirement
     */
    Requirement build(Object args, List<Action> notMetActions, boolean advanced);

    /**
     * Build a requirement
     *
     * @param args args
     * @return requirement
     */
    default Requirement build(Object args) {
        return build(args, null, false);
    }
}
