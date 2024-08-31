package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class YieldIncreaseImpl extends AbstractFertilizerConfig implements YieldIncrease {

    private final List<Pair<Double, Integer>> chances;

    public YieldIncreaseImpl(
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
        super(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions);
        this.chances = chances;
    }

    @Override
    public int amountBonus() {
        for (Pair<Double, Integer> pair : chances) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.YIELD_INCREASE;
    }

    @Override
    public int processDroppedItemAmount(int amount) {
        return amount + amountBonus();
    }
}
