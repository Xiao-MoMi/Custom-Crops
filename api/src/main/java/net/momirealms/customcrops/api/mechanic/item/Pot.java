package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.requirement.State;

import java.util.HashSet;

public interface Pot extends KeyItem {

    int getStorage();

    String getKey();

    void trigger(ActionTrigger trigger, State state);

    HashSet<String> getPotBlocks();

    PassiveFillMethod[] getPassiveFillMethods();

    String getDryItem();

    String getWetItem();
}
