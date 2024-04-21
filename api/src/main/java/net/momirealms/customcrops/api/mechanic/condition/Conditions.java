package net.momirealms.customcrops.api.mechanic.condition;

public class Conditions {

    private final Condition[] conditions;

    public Conditions(Condition[] conditions) {
        this.conditions = conditions;
    }

    /**
     * Get a list of conditions
     *
     * @return conditions
     */
    public Condition[] getConditions() {
        return conditions;
    }
}
