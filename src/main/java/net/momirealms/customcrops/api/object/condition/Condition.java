package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.api.object.world.SimpleLocation;

public interface Condition {

    boolean isMet(SimpleLocation simpleLocation);
}
