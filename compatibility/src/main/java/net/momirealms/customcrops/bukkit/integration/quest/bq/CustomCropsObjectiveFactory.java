package net.momirealms.customcrops.bukkit.integration.quest.bq;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * A factory class responsible for parsing quest instructions and creating CustomCrops objectives.
 */
public class CustomCropsObjectiveFactory implements ObjectiveFactory {

    private final ObjectiveCreator creator;

    /**
     * @param creator the strategy used to instantiate the objective
     */
    public CustomCropsObjectiveFactory(ObjectiveCreator creator) {
        this.creator = creator;
    }

    /**
     * @param instruction the instruction object to parse
     * @return the created objective instance
     * @throws QuestException if mandatory arguments are missing or invalid
     */
    @Override
    public DefaultObjective parseInstruction(Instruction instruction) throws QuestException {
        final Argument<List<String>> identifiers = instruction.string().list().get();
        final Argument<List<String>> targets = instruction.string().list().get("targets", List.of());
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        return creator.create(instruction, targetAmount, identifiers, targets);
    }

    /**
     * Functional interface for objective instantiation.
     */
    @FunctionalInterface
    public interface ObjectiveCreator {
        /**
         * @param instruction        the instruction for the objective
         * @param amount      the required amount for completion
         * @param identifiers the list of allowed tool or item identifiers
         * @param targets     the list of allowed target identifiers
         * @return the new objective instance
         * @throws QuestException if initialization fails
         */
        DefaultObjective create(Instruction instruction, Argument<Number> amount,
                                Argument<List<String>> identifiers, Argument<List<String>> targets) throws QuestException;
    }
}