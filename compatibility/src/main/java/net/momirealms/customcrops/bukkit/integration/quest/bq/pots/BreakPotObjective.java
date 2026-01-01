package net.momirealms.customcrops.bukkit.integration.quest.bq.pots;

import net.momirealms.customcrops.api.event.PotBreakEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class BreakPotObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public BreakPotObjective(
            final Instruction instruction,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(instruction, targetAmount, "custom_crops_to_break_pot");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakPot(PotBreakEvent event) throws QuestException {
        if (!(event.entityBreaker() instanceof Player player)) {
            return;
        }
        OnlineProfile profile = profileProvider.getProfile(player);
        if (!containsPlayer(profile)) {
            return;
        }
        if (!checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.potConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }

}
