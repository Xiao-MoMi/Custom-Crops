package net.momirealms.customcrops.scheduler.task;

import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;

public class ReplaceTask {

    private final SimpleLocation simpleLocation;
    private final ItemCarrier carrier;
    private final String id;

    public ReplaceTask(SimpleLocation simpleLocation, ItemCarrier carrier, String id) {
        this.simpleLocation = simpleLocation;
        this.carrier = carrier;
        this.id = id;
    }

    public SimpleLocation getSimpleLocation() {
        return simpleLocation;
    }

    public ItemCarrier getCarrier() {
        return carrier;
    }

    public String getId() {
        return id;
    }
}
