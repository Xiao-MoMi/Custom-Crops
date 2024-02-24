package net.momirealms.customcrops.api.common.item;

import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.requirement.State;

public interface EventItem {

    void trigger(ActionTrigger actionTrigger, State state);

}
