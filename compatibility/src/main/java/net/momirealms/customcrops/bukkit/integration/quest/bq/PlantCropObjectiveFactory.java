package net.momirealms.customcrops.bukkit.integration.quest.bq;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

public class PlantCropObjectiveFactory implements ObjectiveFactory {

    public PlantCropObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<List<String>> names = instruction.getList(Argument.STRING);
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        return new PlantCropObjective(instruction, targetAmount, names);
    }
}
