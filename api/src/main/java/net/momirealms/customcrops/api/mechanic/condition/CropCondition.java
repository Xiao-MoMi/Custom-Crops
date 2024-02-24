package net.momirealms.customcrops.api.mechanic.condition;

import org.bukkit.Location;

public class CropCondition {

    private final Location location;

    public CropCondition(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
