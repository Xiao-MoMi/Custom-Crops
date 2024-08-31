package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

public abstract class AbstractFertilizerConfig implements FertilizerConfig {

    protected String id;
    protected String itemID;
    protected String icon;
    protected int times;
    protected boolean beforePlant;
    protected Set<String> whitelistPots;
    protected Requirement<Player>[] requirements;
    protected Action<Player>[] beforePlantActions;
    protected Action<Player>[] useActions;
    protected Action<Player>[] wrongPotActions;

    public AbstractFertilizerConfig(
            String id,
            String itemID,
            int times,
            String icon,
            boolean beforePlant,
            Set<String> whitelistPots,
            Requirement<Player>[] requirements,
            Action<Player>[] beforePlantActions,
            Action<Player>[] useActions,
            Action<Player>[] wrongPotActions
    ) {
        this.id = Objects.requireNonNull(id);
        this.itemID = Objects.requireNonNull(itemID);
        this.beforePlant = beforePlant;
        this.times = times;
        this.icon = icon;
        this.requirements = requirements;
        this.whitelistPots = whitelistPots;
        this.beforePlantActions = beforePlantActions;
        this.useActions = useActions;
        this.wrongPotActions = wrongPotActions;
    }

    @Override
    public Action<Player>[] beforePlantActions() {
        return beforePlantActions;
    }

    @Override
    public Action<Player>[] useActions() {
        return useActions;
    }

    @Override
    public Action<Player>[] wrongPotActions() {
        return wrongPotActions;
    }

    @Override
    public boolean beforePlant() {
        return beforePlant;
    }

    @Override
    public Set<String> whitelistPots() {
        return whitelistPots;
    }

    @Override
    public String icon() {
        return icon;
    }

    @Override
    public int times() {
        return times;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Requirement<Player>[] requirements() {
        return requirements;
    }

    @Override
    public String itemID() {
        return itemID;
    }

    @Override
    public int processGainPoints(int previousPoints) {
        return previousPoints;
    }

    @Override
    public int processWaterToLose(int waterToLose) {
        return waterToLose;
    }

    @Override
    public double processVariationChance(double previousChance) {
        return previousChance;
    }

    @Override
    public int processDroppedItemAmount(int amount) {
        return amount;
    }

    @Override
    public double[] overrideQualityRatio() {
        return null;
    }
}
