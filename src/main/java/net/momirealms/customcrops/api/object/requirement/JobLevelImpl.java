package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.integration.JobInterface;
import org.jetbrains.annotations.Nullable;

public class JobLevelImpl extends AbstractRequirement implements Requirement {

    private final int level;

    public JobLevelImpl(@Nullable String[] msg, int level) {
        super(msg);
        this.level = level;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
        JobInterface jobInterface = CustomCrops.getInstance().getIntegrationManager().getJobInterface();
        if (jobInterface == null || currentState.getPlayer() == null) return true;
        if (jobInterface.getLevel(currentState.getPlayer()) >= level) {
            return true;
        }
        notMetMessage(currentState.getPlayer());
        return false;
    }
}
