package net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcan;

import net.momirealms.customcrops.api.event.WateringCanFillEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FillCanObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public FillCanObjective(
            final ObjectiveFactoryService service,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(service, targetAmount, "customcrops.can_fill");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFillWateringCan(WateringCanFillEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.wateringCanConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
