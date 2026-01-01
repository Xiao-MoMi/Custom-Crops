package net.momirealms.customcrops.bukkit.integration.quest.bq.pots;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

public class PlacePotObjectiveFactory implements ObjectiveFactory {

    public PlacePotObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<List<String>> names = instruction.string().list().get();
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        return new PlacePotObjective(instruction, targetAmount, names);
    }
}
