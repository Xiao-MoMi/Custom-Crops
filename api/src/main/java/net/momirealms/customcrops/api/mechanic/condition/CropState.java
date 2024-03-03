package net.momirealms.customcrops.api.mechanic.condition;

import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;

public class CropState {

    private final SimpleLocation location;

    public CropState(SimpleLocation location) {
        this.location = location;
    }

    public SimpleLocation getLocation() {
        return location;
    }
}
