package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.api.object.world.SimpleLocation;

import java.util.List;

public class AndCondition implements Condition {

    private final List<Condition> deathConditions;

    public AndCondition(List<Condition> deathConditions) {
        this.deathConditions = deathConditions;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        for (Condition condition : deathConditions) {
            if (!condition.isMet(simpleLocation)) {
                return false;
            }
        }
        return true;
    }
}