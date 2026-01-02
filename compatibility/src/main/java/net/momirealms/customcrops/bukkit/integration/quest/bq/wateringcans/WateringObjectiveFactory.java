package net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcans;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

public class WateringObjectiveFactory implements ObjectiveFactory {

    private final ObjectiveCreator creator;

    public WateringObjectiveFactory(ObjectiveCreator creator) {
        this.creator = creator;
    }

    @Override
    public DefaultObjective parseInstruction(Instruction instruction) throws QuestException {
        final Argument<List<String>> canIDs = instruction.string().list().get();
        final Argument<List<String>> targetIDs = instruction.string().list().get();
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        return creator.create(instruction, targetAmount, canIDs, targetIDs);
    }

    @FunctionalInterface
    public interface ObjectiveCreator {
        DefaultObjective create(Instruction inst, Argument<Number> amount,
                                Argument<List<String>> cans, Argument<List<String>> targets) throws QuestException;
    }
}