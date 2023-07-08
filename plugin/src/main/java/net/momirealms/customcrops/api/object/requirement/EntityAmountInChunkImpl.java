package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.customcrops.api.object.action.Action;
import org.jetbrains.annotations.Nullable;

public class EntityAmountInChunkImpl extends AbstractRequirement implements Requirement {

    private final int amount;

    public EntityAmountInChunkImpl(@Nullable String[] msg, @Nullable Action[] actions, int amount) {
        super(msg, actions);
        this.amount = amount;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
        if (currentState.getLocation().getChunk().getEntities().length <= amount) {
            return true;
        }
        notMetActions(currentState);
        return false;
    }
}
