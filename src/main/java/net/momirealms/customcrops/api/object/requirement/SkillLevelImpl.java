package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.integration.SkillInterface;
import org.jetbrains.annotations.Nullable;

public class SkillLevelImpl extends AbstractRequirement implements Requirement {

    private final int level;

    public SkillLevelImpl(@Nullable String[] msg, int level) {
        super(msg);
        this.level = level;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
         SkillInterface skillInterface = CustomCrops.getInstance().getIntegrationManager().getSkillInterface();
         if (skillInterface == null || currentState.getPlayer() == null) return true;
         if (skillInterface.getLevel(currentState.getPlayer()) >= level) {
             return true;
         }
         notMetMessage(currentState.getPlayer());
         return false;
    }
}
