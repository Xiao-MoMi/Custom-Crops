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

package net.momirealms.customcrops.api.mechanic.item.water;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;

public class PositiveFillMethod extends AbstractFillMethod {

    private final String id;

    public PositiveFillMethod(String id, int amount, Action[] actions, Requirement[] requirements) {
        super(amount, actions, requirements);
        this.id = id;
    }

    /**
     * Get the block/furniture ID
     *
     * @return id
     */
    public String getID() {
        return id;
    }
}
