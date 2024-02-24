package net.momirealms.customcrops.mechanic.item.impl;

import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class SprinklerConfig extends AbstractEventItem implements Sprinkler {

    private final String key;
    private final int range;
    private final int storage;
    private final int water;
    private final boolean infinite;
    private final String twoDItem;
    private final String threeDItem;
    private final WaterBar waterBar;
    private final HashSet<String> potWhitelist;
    private final ItemCarrier itemCarrier;
    private final PassiveFillMethod[] passiveFillMethods;
    private final Requirement[] requirements;

    public SprinklerConfig(
            String key,
            ItemCarrier itemCarrier,
            String twoDItem,
            String threeDItem,
            int range,
            int storage,
            int water,
            boolean infinite,
            WaterBar waterBar,
            HashSet<String> potWhitelist,
            PassiveFillMethod[] passiveFillMethods,
            HashMap<ActionTrigger, Action[]> actionMap,
            Requirement[] requirements
    ) {
        super(actionMap);
        this.key = key;
        this.itemCarrier = itemCarrier;
        this.twoDItem = twoDItem;
        this.threeDItem = threeDItem;
        this.range = range;
        this.storage = storage;
        this.infinite = infinite;
        this.water = water;
        this.waterBar = waterBar;
        this.potWhitelist = potWhitelist;
        this.passiveFillMethods = passiveFillMethods;
        this.requirements = requirements;
    }

    @Nullable
    @Override
    public String get2DItemID() {
        return twoDItem;
    }

    @NotNull
    @Override
    public String get3DItemID() {
        return threeDItem;
    }

    @Override
    public int getStorage() {
        return storage;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean isInfinite() {
        return infinite;
    }

    @Override
    public int getWater() {
        return water;
    }

    @Override
    public HashSet<String> getPotWhitelist() {
        return potWhitelist;
    }

    @Override
    public ItemCarrier getItemCarrier() {
        return itemCarrier;
    }

    @Override
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }

    @Override
    public Requirement[] getRequirements() {
        return requirements;
    }
}
