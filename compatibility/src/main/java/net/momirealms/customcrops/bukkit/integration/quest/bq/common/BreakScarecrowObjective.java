package net.momirealms.customcrops.bukkit.integration.quest.bq.common;

import net.momirealms.customcrops.api.event.ScarecrowBreakEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class BreakScarecrowObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public BreakScarecrowObjective(
            final ObjectiveFactoryService service,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(service, targetAmount, "customcrops.scarecrow_broken");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakScarecrow(ScarecrowBreakEvent event) throws QuestException {
        if (!(event.entityBreaker() instanceof Player player)) {
            return;
        }
        OnlineProfile profile = profileProvider.getProfile(player);
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.scarecrowItemID())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
