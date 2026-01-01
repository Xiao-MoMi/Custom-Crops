package net.momirealms.customcrops.bukkit.integration.quest.bq.crops;

import net.momirealms.customcrops.api.event.CropPlantEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlantCropObjective extends CountingObjective implements Listener {

    private final Argument<List<String>> identifiers;

    public PlantCropObjective(final Instruction instruction, final Argument<Number> targetAmount,
                              final Argument<List<String>> identifiers) throws QuestException {
        super(instruction, targetAmount, "custom_crops_to_plant");
        this.identifiers = identifiers;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlantCrop(CropPlantEvent event) throws QuestException {
        OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(profile)) {
            return;
        }
        if (!checkConditions(profile)) {
            return;
        }
        if (this.identifiers.getValue(profile).contains(event.cropConfig().id())) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
