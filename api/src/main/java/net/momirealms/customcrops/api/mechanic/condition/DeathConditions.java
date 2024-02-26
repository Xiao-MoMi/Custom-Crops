package net.momirealms.customcrops.api.mechanic.condition;

import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;

public class DeathConditions extends Conditions {

    private final String deathItem;
    private final ItemCarrier itemCarrier;

    public DeathConditions(Condition[] conditions, String deathItem, ItemCarrier itemCarrier) {
        super(conditions);
        this.deathItem = deathItem;
        this.itemCarrier = itemCarrier;
    }

    public String getDeathItem() {
        return deathItem;
    }

    public ItemCarrier getItemCarrier() {
        return itemCarrier;
    }
}
