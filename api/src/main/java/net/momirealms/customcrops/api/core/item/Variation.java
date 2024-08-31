package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Variation extends FertilizerConfig {

    double chanceBonus();

    boolean addOrMultiply();

    static Variation create(
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
            boolean addOrMultiply,
            double chance
    ) {
        return new VariationImpl(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions, addOrMultiply, chance);
    }
}
