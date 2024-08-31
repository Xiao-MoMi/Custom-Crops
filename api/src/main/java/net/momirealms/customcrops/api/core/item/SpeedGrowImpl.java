package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class SpeedGrowImpl extends AbstractFertilizerConfig implements SpeedGrow {

    private final List<Pair<Double, Integer>> chances;

    public SpeedGrowImpl(
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
    public int pointBonus() {
        for (Pair<Double, Integer> pair : chances) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.SPEED_GROW;
    }

    @Override
    public int processGainPoints(int previousPoints) {
        return pointBonus() + previousPoints;
    }
}
