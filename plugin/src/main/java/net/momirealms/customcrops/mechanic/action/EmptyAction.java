package net.momirealms.customcrops.mechanic.action;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.requirement.State;

/**
 * An implementation of the Action interface that represents an empty action with no behavior.
 * This class serves as a default action to prevent NPE.
 */
public class EmptyAction implements Action {

    public static EmptyAction instance = new EmptyAction();

    @Override
    public void trigger(State state) {
    }
}
