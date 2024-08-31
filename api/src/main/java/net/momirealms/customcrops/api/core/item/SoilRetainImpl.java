package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Set;

public class SoilRetainImpl extends AbstractFertilizerConfig implements SoilRetain {

    private final double chance;

    public SoilRetainImpl(
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
            double chance
    ) {
        super(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions);
        this.chance = chance;
    }

    @Override
    public double chance() {
        return chance;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.SOIL_RETAIN;
    }

    @Override
    public int processWaterToLose(int waterToLose) {
        return Math.min(waterToLose, 0);
    }
}
