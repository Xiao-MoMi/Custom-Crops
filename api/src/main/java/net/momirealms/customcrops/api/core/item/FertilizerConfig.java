package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface FertilizerConfig {

    String id();

    FertilizerType type();

    boolean beforePlant();

    String icon();

    Requirement<Player>[] requirements();

    String itemID();

    int times();

    Set<String> whitelistPots();

    Action<Player>[] beforePlantActions();

    Action<Player>[] useActions();

    Action<Player>[] wrongPotActions();

    int processGainPoints(int previousPoints);

    int processWaterToLose(int waterToLose);

    double processVariationChance(double previousChance);

    int processDroppedItemAmount(int amount);

    @Nullable
    double[] overrideQualityRatio();
}
