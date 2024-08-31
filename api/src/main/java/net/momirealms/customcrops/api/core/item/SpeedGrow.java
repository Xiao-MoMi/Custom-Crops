package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface SpeedGrow extends FertilizerConfig {

    int pointBonus();

    static SpeedGrow create(
            String id,
            String itemID,
            int times,
            String icon,
            boolean beforePlant,
            Set<String> whitelistPots,
            Requirement<Player>[] requirements,
            Action<Player>[] beforePlantActions,
            Action<Player>[] useActions,
            Action<Player>[] wrongPotActions,
            List<Pair<Double, Integer>> chances
    ) {
        return new SpeedGrowImpl(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions, chances);
    }
}
