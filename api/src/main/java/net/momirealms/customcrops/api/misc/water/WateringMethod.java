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
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class WateringMethod extends AbstractMethod {

    private final String used;
    private final int usedAmount;
    private final String returned;
    private final int returnedAmount;

    public WateringMethod(
            String used,
            int usedAmount,
            @Nullable String returned,
            int returnedAmount,
            int amount,
            Action<Player>[] actions,
            Requirement<Player>[] requirements
    ) {
        super(amount, actions, requirements);
        this.used = used;
        this.returned = returned;
        this.usedAmount = usedAmount;
        this.returnedAmount = returnedAmount;
    }

    /**
     * Get the consumed item ID
     *
     * @return consumed item ID
     */
    public String getUsed() {
        return used;
    }

    /**
     * Get the returned item ID
     *
     * @return returned item ID
     */
    public String getReturned() {
        return returned;
    }

    /**
     * Get the amount to consume
     *
     * @return amount to consume
     */
    public int getUsedAmount() {
        return usedAmount;
    }

    /**
     * Get the amount of the returned items
     *
     * @return amount of the returned items
     */
    public int getReturnedAmount() {
        return returnedAmount;
    }
}
