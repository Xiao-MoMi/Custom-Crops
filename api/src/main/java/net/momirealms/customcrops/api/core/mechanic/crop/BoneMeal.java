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

package net.momirealms.customcrops.api.core.mechanic.crop;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;

public class BoneMeal {

    private final String item;
    private final int requiredAmount;
    private final String returned;
    private final int returnedAmount;
    private final List<Pair<Double, Integer>> pointGainList;
    private final Action<Player>[] actions;
    private final boolean dispenserAllowed;

    public BoneMeal(
            String item,
            int requiredAmount,
            String returned,
            int returnedAmount,
            boolean dispenserAllowed,
            List<Pair<Double, Integer>> pointGainList,
            Action<Player>[] actions
    ) {
        this.item = item;
        this.returned = returned;
        this.pointGainList = pointGainList;
        this.actions = actions;
        this.requiredAmount = requiredAmount;
        this.returnedAmount = returnedAmount;
        this.dispenserAllowed = dispenserAllowed;
    }

    public String requiredItem() {
        return item;
    }

    public String returnedItem() {
        return returned;
    }

    public int rollPoint() {
        for (Pair<Double, Integer> pair : pointGainList) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    public void triggerActions(Context<Player> context) {
        ActionManager.trigger(context, actions);
    }

    public int amountOfRequiredItem() {
        return requiredAmount;
    }

    public int amountOfReturnItem() {
        return returnedAmount;
    }

    public boolean isDispenserAllowed() {
        return dispenserAllowed;
    }
}
