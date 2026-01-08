package net.momirealms.customcrops.bukkit.integration.quest.bq.fertilizer;

import net.momirealms.customcrops.api.event.FertilizerUseEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FertilizerUseObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;
    private final Argument<List<String>> potIDList;

    public FertilizerUseObjective(
            final ObjectiveFactoryService service,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers,
            final Argument<List<String>> potIDList
    ) throws QuestException {
        super(service, targetAmount, "customcrops.use_fertilizer");
        this.identifiers = identifiers;
        this.potIDList = potIDList;
    }

    @EventHandler(ignoreCancelled = true)
    public void onUseFertilizer(FertilizerUseEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }

        if (!this.identifiers.getValue(profile).contains(event.fertilizer().id())) {
            return;
        }

        List<String> allowedPots = this.potIDList.getValue(profile);
        if (allowedPots.isEmpty() || allowedPots.contains(event.potConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
