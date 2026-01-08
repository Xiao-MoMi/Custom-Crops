package net.momirealms.customcrops.bukkit.integration.quest.bq;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * A simplified factory for creating CustomCrops objectives that do not require target filters.
 */
public class SimpleCustomCropsObjectiveFactory implements ObjectiveFactory {

    private final ObjectiveCreator creator;

    /**
     * @param creator the strategy used to instantiate the objective
     */
    public SimpleCustomCropsObjectiveFactory(ObjectiveCreator creator) {
        this.creator = creator;
    }

    /**
     * @param instruction the instruction object to parse
     * @return the created objective instance
     * @throws QuestException if mandatory arguments are missing or invalid
     */
    @Override
    public DefaultObjective parseInstruction(Instruction instruction, ObjectiveFactoryService service) throws QuestException {
        final Argument<List<String>> identifiers = instruction.string().list().get();
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        return creator.create(service, targetAmount, identifiers);
    }

    /**
     * Functional interface for simple objective instantiation.
     */
    @FunctionalInterface
    public interface ObjectiveCreator {
        /**
         * @param service     the service for the objective
         * @param amount      the required amount for completion
         * @param identifiers the list of allowed identifiers
         * @return the new objective instance
         * @throws QuestException if initialization fails
         */
        DefaultObjective create(ObjectiveFactoryService service, Argument<Number> amount,
                                Argument<List<String>> identifiers) throws QuestException;
    }
}