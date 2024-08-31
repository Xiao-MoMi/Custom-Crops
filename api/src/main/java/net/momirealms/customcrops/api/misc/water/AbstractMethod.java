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

package net.momirealms.customcrops.api.misc.water;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import org.bukkit.entity.Player;

public abstract class AbstractMethod {

    protected int amount;
    private final Action<Player>[] actions;
    private final Requirement<Player>[] requirements;

    protected AbstractMethod(int amount, Action<Player>[] actions, Requirement<Player>[] requirements) {
        this.amount = amount;
        this.actions = actions;
        this.requirements = requirements;
    }

    public int amountOfWater() {
        return amount;
    }

    public void triggerActions(Context<Player> context) {
        ActionManager.trigger(context, actions);
    }

    public boolean checkRequirements(Context<Player> context) {
        return RequirementManager.isSatisfied(context, requirements);
    }
}
