package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Quality extends FertilizerConfig {

    double chance();

    double[] ratio();

    static Quality create(
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
            double chance,
            double[] ratio
    ) {
        return new QualityImpl(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions, chance, ratio);
    }
}
