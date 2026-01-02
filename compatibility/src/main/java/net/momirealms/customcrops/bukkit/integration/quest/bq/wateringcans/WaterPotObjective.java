package net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcans;

import net.momirealms.customcrops.api.event.WateringCanWaterPotEvent;
import net.momirealms.customcrops.common.util.Pair;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class WaterPotObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;
    private final Argument<List<String>> potIDList;

    public WaterPotObjective(
            final Instruction instruction,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers,
            final Argument<List<String>> potIDList
    ) throws QuestException {
        super(instruction, targetAmount, "customcrops.can_pot");
        this.identifiers = identifiers;
        this.potIDList = potIDList;
    }

    @EventHandler(ignoreCancelled = true)
    public void onWateringPot(WateringCanWaterPotEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }

        if (!this.identifiers.getValue(profile).contains(event.wateringCanConfig().id())) {
            return;
        }

        List<String> allowedPots = this.potIDList.getValue(profile);
        if (allowedPots.isEmpty() || allowedPots.contains(event.potConfig().id())){
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
