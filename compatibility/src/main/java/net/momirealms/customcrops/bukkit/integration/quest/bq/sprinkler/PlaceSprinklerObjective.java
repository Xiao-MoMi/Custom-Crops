package net.momirealms.customcrops.bukkit.integration.quest.bq.sprinkler;

import net.momirealms.customcrops.api.event.SprinklerBreakEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlaceSprinklerObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public PlaceSprinklerObjective(
            final ObjectiveFactoryService service,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(service, targetAmount, "customcrops.sprinkler_placed");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakSprinkler(SprinklerBreakEvent event) throws QuestException {
        if (!(event.entityBreaker() instanceof Player player)) {
            return;
        }
        OnlineProfile profile = profileProvider.getProfile(player);
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.sprinklerConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }

}
