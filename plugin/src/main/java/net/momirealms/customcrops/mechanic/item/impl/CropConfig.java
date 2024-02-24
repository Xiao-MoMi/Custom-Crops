package net.momirealms.customcrops.mechanic.item.impl;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;

import java.util.HashMap;

public class CropConfig extends AbstractEventItem implements Crop {

    private final String key;
    private final String seedID;

    public CropConfig(
            String key,
            String seedID,
            HashMap<ActionTrigger, Action[]> actionMap
    ) {
        super(actionMap);
        this.key = key;
        this.seedID = seedID;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getSeedItemID() {
        return seedID;
    }
}
