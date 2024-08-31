package net.momirealms.customcrops.api.core.block;

import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;

public class GrowCondition {

    private final Requirement<CustomCropsBlockState>[] requirements;
    private final int pointToAdd;

    public GrowCondition(Requirement<CustomCropsBlockState>[] requirements, int pointToAdd) {
        this.requirements = requirements;
        this.pointToAdd = pointToAdd;
    }

    public int pointToAdd() {
        return pointToAdd;
    }

    public boolean isMet(Context<CustomCropsBlockState> context) {
        return RequirementManager.isSatisfied(context, requirements);
    }
}
