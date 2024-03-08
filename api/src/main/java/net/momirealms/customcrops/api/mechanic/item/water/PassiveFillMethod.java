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
import org.jetbrains.annotations.Nullable;

public class PassiveFillMethod extends AbstractFillMethod {

    private final String used;
    private final int usedAmount;
    private final String returned;
    private final int returnedAmount;

    public PassiveFillMethod(String used, int usedAmount, @Nullable String returned, int returnedAmount, int amount, Action[] actions, Requirement[] requirements) {
        super(amount, actions, requirements);
        this.used = used;
        this.returned = returned;
        this.usedAmount = usedAmount;
        this.returnedAmount = returnedAmount;
    }

    public String getUsed() {
        return used;
    }

    public String getReturned() {
        return returned;
    }

    public int getUsedAmount() {
        return usedAmount;
    }

    public int getReturnedAmount() {
        return returnedAmount;
    }
}
