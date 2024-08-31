package net.momirealms.customcrops.api.core.block;

import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import org.jetbrains.annotations.Nullable;

public class DeathCondition {

    private final Requirement<CustomCropsBlockState>[] requirements;
    private final String deathStage;
    private final ExistenceForm existenceForm;
    private final int deathDelay;

    public DeathCondition(Requirement<CustomCropsBlockState>[] requirements, String deathStage, ExistenceForm existenceForm, int deathDelay) {
        this.requirements = requirements;
        this.deathStage = deathStage;
        this.existenceForm = existenceForm;
        this.deathDelay = deathDelay;
    }

    @Nullable
    public String deathStage() {
        return deathStage;
    }

    public int deathDelay() {
        return deathDelay;
    }

    public boolean isMet(Context<CustomCropsBlockState> context) {
        return RequirementManager.isSatisfied(context, requirements);
    }

    public ExistenceForm existenceForm() {
        return existenceForm;
    }
}
