package net.momirealms.customcrops.bukkit.integration.quest.bq.pots;

import net.momirealms.customcrops.bukkit.integration.quest.bq.SimpleCustomCropsObjectiveFactory;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * @deprecated This factory is currently under review for potential replacement by
 * {@link SimpleCustomCropsObjectiveFactory} to unify the objective creation logic.
 */
@Deprecated(since = "Next Version", forRemoval = true)
public class BreakPotObjectiveFactory implements ObjectiveFactory {

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<List<String>> names = instruction.string().list().get();
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        return new BreakPotObjective(instruction, targetAmount, names);
    }
}
