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

package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.manager.ActionManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.requirement.State;

import java.util.List;

public class BoneMeal {

    private final String item;
    private final int usedAmount;
    private final String returned;
    private final int returnedAmount;
    private final List<Pair<Double, Integer>> pointGainList;
    private final Action[] actions;
    private final boolean dispenserAllowed;

    public BoneMeal(
            String item,
            int usedAmount,
            String returned,
            int returnedAmount,
            boolean dispenserAllowed,
            List<Pair<Double, Integer>> pointGainList,
            Action[] actions
    ) {
        this.item = item;
        this.returned = returned;
        this.pointGainList = pointGainList;
        this.actions = actions;
        this.usedAmount = usedAmount;
        this.returnedAmount = returnedAmount;
        this.dispenserAllowed = dispenserAllowed;
    }

    public String getItem() {
        return item;
    }

    public String getReturned() {
        return returned;
    }

    public int getPoint() {
        for (Pair<Double, Integer> pair : pointGainList) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    public void trigger(State state) {
        ActionManager.triggerActions(state, actions);
    }

    public int getUsedAmount() {
        return usedAmount;
    }

    public int getReturnedAmount() {
        return returnedAmount;
    }

    public boolean isDispenserAllowed() {
        return dispenserAllowed;
    }
}
