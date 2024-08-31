package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Set;

public class QualityImpl extends AbstractFertilizerConfig implements Quality {

    private final double chance;
    private final double[] ratio;

    protected QualityImpl(
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
        super(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions);
        this.chance = chance;
        this.ratio = ratio;
    }

    @Override
    public double chance() {
        return chance;
    }

    @Override
    public double[] ratio() {
        return ratio;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.QUALITY;
    }

    @Override
    public double[] overrideQualityRatio() {
        return ratio();
    }
}
