package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.api.context.Context;

@FunctionalInterface
public interface StatedItem<T> {

    String currentState(Context<T> context);
}
