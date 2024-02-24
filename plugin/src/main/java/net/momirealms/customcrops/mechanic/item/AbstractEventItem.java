package net.momirealms.customcrops.mechanic.item;

import net.momirealms.customcrops.api.manager.ActionManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.common.item.EventItem;
import net.momirealms.customcrops.api.mechanic.requirement.State;

import java.util.HashMap;

public abstract class AbstractEventItem implements EventItem {

    private final HashMap<ActionTrigger, Action[]> actionMap;

    public AbstractEventItem(HashMap<ActionTrigger, Action[]> actionMap) {
        this.actionMap = actionMap;
    }

    @Override
    public void trigger(ActionTrigger actionTrigger, State state) {
        Action[] actions = actionMap.get(actionTrigger);
        if (actions != null) {
            ActionManager.triggerActions(state, actions);
        }
    }
}
