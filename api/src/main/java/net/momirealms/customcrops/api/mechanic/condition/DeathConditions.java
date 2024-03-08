package net.momirealms.customcrops.api.mechanic.condition;

import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;

public class DeathConditions extends Conditions {

    private final String deathItem;
    private final ItemCarrier itemCarrier;
    private final int deathDelay;

    public DeathConditions(Condition[] conditions, String deathItem, ItemCarrier itemCarrier, int deathDelay) {
        super(conditions);
        this.deathItem = deathItem;
        this.itemCarrier = itemCarrier;
        this.deathDelay = deathDelay;
    }

    public String getDeathItem() {
        return deathItem;
    }

    public ItemCarrier getItemCarrier() {
        return itemCarrier;
    }

    public int getDeathDelay() {
        return deathDelay;
    }
}
