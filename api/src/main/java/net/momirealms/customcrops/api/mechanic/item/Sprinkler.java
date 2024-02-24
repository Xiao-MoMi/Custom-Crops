package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;

import java.util.HashSet;

public interface Sprinkler extends KeyItem {

    String get2DItemID();

    String get3DItemID();

    int getStorage();

    int getRange();

    boolean isInfinite();

    int getWater();

    HashSet<String> getPotWhitelist();

    ItemCarrier getItemCarrier();

    PassiveFillMethod[] getPassiveFillMethods();

    Requirement[] getRequirements();
}
