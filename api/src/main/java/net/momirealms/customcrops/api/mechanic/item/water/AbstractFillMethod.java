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

import net.momirealms.customcrops.api.manager.ActionManager;
import net.momirealms.customcrops.api.manager.RequirementManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.State;

public abstract class AbstractFillMethod {

    protected int amount;
    private final Action[] actions;
    private final Requirement[] requirements;

    protected AbstractFillMethod(int amount, Action[] actions, Requirement[] requirements) {
        this.amount = amount;
        this.actions = actions;
        this.requirements = requirements;
    }

    /**
     * Get the amount of water to add
     *
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Trigger actions related to this fill methods
     *
     * @param state state
     */
    public void trigger(State state) {
        ActionManager.triggerActions(state, actions);
    }

    /**
     * If player meet the requirements for this fill method
     *
     * @param state state
     * @return meet or not
     */
    public boolean canFill(State state) {
        return RequirementManager.isRequirementMet(state, requirements);
    }
}
