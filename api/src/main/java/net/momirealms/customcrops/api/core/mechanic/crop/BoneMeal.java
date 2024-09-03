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

/**
 * Represents the configuration and behavior of bone meal in the crop mechanic.
 */
public class BoneMeal {

    private final String item;
    private final int requiredAmount;
    private final String returned;
    private final int returnedAmount;
    private final List<Pair<Double, Integer>> pointGainList;
    private final Action<Player>[] actions;
    private final boolean dispenserAllowed;

    /**
     * Constructs a new BoneMeal instance with the specified properties.
     *
     * @param item             The identifier for the item required to use this bone meal.
     * @param requiredAmount   The amount of the required item needed for applying bone meal.
     * @param returned         The identifier for the item returned after bone meal usage.
     * @param returnedAmount   The amount of the returned item given back after using bone meal.
     * @param dispenserAllowed Whether this bone meal can be used by a dispenser.
     * @param pointGainList    A list of pairs representing the probability and the corresponding
     *                         points to gain when bone meal is applied.
     * @param actions          An array of {@link Action} instances to trigger when bone meal is used.
     */
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

    /**
     * Retrieves the identifier of the item required for using this bone meal.
     *
     * @return The required item identifier.
     */
    public String requiredItem() {
        return item;
    }

    /**
     * Retrieves the identifier of the item returned after using this bone meal.
     *
     * @return The returned item identifier.
     */
    public String returnedItem() {
        return returned;
    }

    /**
     * Randomly determines the points gained from applying the bone meal,
     * based on the defined probability and point pairs.
     *
     * @return The points gained from applying the bone meal.
     */
    public int rollPoint() {
        for (Pair<Double, Integer> pair : pointGainList) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    /**
     * Triggers the associated actions when the bone meal is applied.
     *
     * @param context The context in which the actions are triggered.
     */
    public void triggerActions(Context<Player> context) {
        ActionManager.trigger(context, actions);
    }

    /**
     * Retrieves the amount of the required item needed to use this bone meal.
     *
     * @return The required item amount.
     */
    public int amountOfRequiredItem() {
        return requiredAmount;
    }

    /**
     * Retrieves the amount of the returned item after using this bone meal.
     *
     * @return The returned item amount.
     */
    public int amountOfReturnItem() {
        return returnedAmount;
    }

    /**
     * Checks if this bone meal can be used by a dispenser.
     *
     * @return True if the bone meal is dispenser allowed; false otherwise.
     */
    public boolean isDispenserAllowed() {
        return dispenserAllowed;
    }
}
