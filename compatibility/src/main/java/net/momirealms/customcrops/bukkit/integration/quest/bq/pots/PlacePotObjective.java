package net.momirealms.customcrops.bukkit.integration.quest.bq.pots;

import net.momirealms.customcrops.api.event.PotPlaceEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlacePotObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public PlacePotObjective(
            final Instruction instruction,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(instruction, targetAmount, "custom_crops_to_place_pot");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlacePot(PotPlaceEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
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
