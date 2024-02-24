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
