package net.momirealms.customcrops.api.mechanic.condition;

public class OrCondition implements Condition {

    private final Condition[] deathConditions;

    public OrCondition(Condition[] deathConditions) {
        this.deathConditions = deathConditions;
    }

    @Override
    public boolean isConditionMet(CropState cropState) {
        for (Condition condition : deathConditions) {
            if (condition.isConditionMet(cropState)) {
                return true;
            }
        }
        return false;
    }
}
