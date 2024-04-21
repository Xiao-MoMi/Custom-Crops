package net.momirealms.customcrops.api.mechanic.condition;

import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Get the item to replace, null if the crop would be removed
     *
     * @return the item to replace
     */
    @Nullable
    public String getDeathItem() {
        return deathItem;
    }

    /**
     * Get the item carrier of the item to replace
     *
     * @return item carrier
     */
    public ItemCarrier getItemCarrier() {
        return itemCarrier;
    }

    /**
     * Get the delay in ticks
     *
     * @return delay
     */
    public int getDeathDelay() {
        return deathDelay;
    }
}
