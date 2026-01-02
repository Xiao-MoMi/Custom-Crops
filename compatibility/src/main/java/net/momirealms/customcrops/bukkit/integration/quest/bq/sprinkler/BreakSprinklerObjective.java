package net.momirealms.customcrops.bukkit.integration.quest.bq.sprinkler;

import net.momirealms.customcrops.api.event.SprinklerPlaceEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class BreakSprinklerObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public BreakSprinklerObjective(
            final Instruction instruction,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(instruction, targetAmount, "customcrops.sprinkler_placed");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceSprinkler(SprinklerPlaceEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.sprinklerConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }

}
