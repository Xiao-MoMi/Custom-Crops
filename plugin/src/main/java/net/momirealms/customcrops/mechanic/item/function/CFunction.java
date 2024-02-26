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

package net.momirealms.customcrops.mechanic.item.function;

import net.momirealms.customcrops.mechanic.item.function.wrapper.ConditionWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CFunction implements Comparable<CFunction> {

    private final Function<ConditionWrapper, FunctionResult> function;
    private final FunctionPriority priority;

    public CFunction(Function<ConditionWrapper, FunctionResult> function, FunctionPriority priority) {
        this.function = function;
        this.priority = priority;
    }

    public FunctionResult apply(ConditionWrapper wrapper) {
        return function.apply(wrapper);
    }

    public Function<ConditionWrapper, FunctionResult> getFunction() {
        return function;
    }

    public FunctionPriority getPriority() {
        return priority;
    }

    @Override
    public int compareTo(@NotNull CFunction o) {
        return Integer.compare(this.priority.ordinal(), o.priority.ordinal());
    }

    public enum FunctionPriority {

        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST;

        public boolean isHigherThan(FunctionPriority priority) {
            return this.ordinal() < priority.ordinal();
        }
    }
}
