package net.momirealms.customcrops.bukkit.integration.quest.bq.crops;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

public class PlantCropObjectiveFactory implements ObjectiveFactory {

    public PlantCropObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<List<String>> names = instruction.string().list().get();
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        return new PlantCropObjective(instruction, targetAmount, names);
    }
}
