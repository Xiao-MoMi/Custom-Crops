package net.momirealms.customcrops.api.mechanic.condition;

import org.bukkit.Location;

public class CropState {

    private final Location location;

    public CropState(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
