package net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcans;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.bukkit.event.Listener;

public class FillCanObjective extends CountingObjective implements Listener {

    private final Argument<String> identifiers;

    public FillCanObjective(
            Instruction instruction,
            final Argument<Number> targetAmount,
            final Argument<String> identifiers
    ) throws QuestException {
        super(instruction, targetAmount, "customcrops.can_fill");
        this.identifiers = identifiers;
    }
}
