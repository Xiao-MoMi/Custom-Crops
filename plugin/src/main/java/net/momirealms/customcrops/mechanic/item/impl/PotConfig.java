package net.momirealms.customcrops.mechanic.item.impl;

import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;

import java.util.HashMap;
import java.util.HashSet;

public class PotConfig extends AbstractEventItem implements Pot {

    private String key;
    private final int storage;
    private final String dryModel;
    private final String wetModel;
    private boolean enableFertilizedAppearance;
    private final HashMap<FertilizerType, Pair<String, String>> fertilizedPotMap;
    private final PassiveFillMethod[] passiveFillMethods;
    private WaterBar waterBar;

    public PotConfig(
            String key,
            int storage,
            String dryModel,
            String wetModel,
            boolean enableFertilizedAppearance,
            HashMap<FertilizerType, Pair<String, String>> fertilizedPotMap,
            WaterBar waterBar,
            PassiveFillMethod[] passiveFillMethods,
            HashMap<ActionTrigger, Action[]> actionMap
    ) {
        super(actionMap);
        this.storage = storage;
        this.enableFertilizedAppearance = enableFertilizedAppearance;
        this.fertilizedPotMap = fertilizedPotMap;
        this.passiveFillMethods = passiveFillMethods;
        this.dryModel = dryModel;
        this.wetModel = wetModel;
        this.waterBar = waterBar;
    }

    @Override
    public int getStorage() {
        return 0;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public HashSet<String> getPotBlocks() {
        HashSet<String> set = new HashSet<>();
        set.add(wetModel);
        set.add(dryModel);
        for (Pair<String, String> pair : fertilizedPotMap.values()) {
            set.add(pair.left());
            set.add(pair.right());
        }
        return set;
    }

    @Override
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }

    @Override
    public String getDryItem() {
        return dryModel;
    }

    @Override
    public String getWetItem() {
        return wetModel;
    }
}
