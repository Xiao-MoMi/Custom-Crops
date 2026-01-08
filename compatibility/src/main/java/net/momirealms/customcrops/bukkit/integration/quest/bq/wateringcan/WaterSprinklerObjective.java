package net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcan;

import net.momirealms.customcrops.api.event.WateringCanWaterSprinklerEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class WaterSprinklerObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;
    private final Argument<List<String>> sprinklerIDList;

    public WaterSprinklerObjective(
            final ObjectiveFactoryService service,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers,
            final Argument<List<String>> sprinklerIDList
    ) throws QuestException {
        super(service, targetAmount, "customcrops.can_sprinkler");
        this.identifiers = identifiers;
        this.sprinklerIDList = sprinklerIDList;
    }

    @EventHandler(ignoreCancelled = true)
    public void onWateringSprinkler(WateringCanWaterSprinklerEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }

        if (!this.identifiers.getValue(profile).contains(event.wateringCanConfig().id())) {
            return;
        }

        List<String> allowedSprinklers = this.sprinklerIDList.getValue(profile);
        if (allowedSprinklers.isEmpty() || allowedSprinklers.contains(event.sprinklerConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
