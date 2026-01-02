package net.momirealms.customcrops.bukkit.integration.quest.bq.crops;

import net.momirealms.customcrops.api.event.CropBreakEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class HarvestCropObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public HarvestCropObjective(
            final Instruction instruction,
            final Argument<Number> targetAmount,
            final Argument<List<String>> identifiers
    ) throws QuestException {
        super(instruction, targetAmount, "customcrops.crop_harvested");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCrop(CropBreakEvent event) throws QuestException {
        if (!(event.entityBreaker() instanceof Player player)) {
            return;
        }
        OnlineProfile profile = profileProvider.getProfile(player);
        if (!containsPlayer(profile) || !checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.cropStageItemID())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
