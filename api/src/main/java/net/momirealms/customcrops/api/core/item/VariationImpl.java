package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Set;

public class VariationImpl extends AbstractFertilizerConfig implements Variation {

    private final double chance;
    private final boolean addOrMultiply;

    public VariationImpl(
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
        super(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions);
        this.chance = chance;
        this.addOrMultiply = addOrMultiply;
    }

    @Override
    public double chanceBonus() {
        return chance;
    }

    @Override
    public boolean addOrMultiply() {
        return addOrMultiply;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.VARIATION;
    }

    @Override
    public double processVariationChance(double previousChance) {
        if (addOrMultiply()) {
            return previousChance + chanceBonus();
        } else {
            return previousChance * chanceBonus();
        }
    }
}
