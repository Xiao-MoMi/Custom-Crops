package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.EventItem;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;

import java.util.HashSet;

public interface Fertilizer extends EventItem {
    String getKey();

    String getItemID();

    int getTimes();

    FertilizerType getFertilizerType();

    HashSet<String> getPotWhitelist();

    boolean isBeforePlant();

    String getIcon();

    Requirement[] getRequirements();
}
