package net.momirealms.customcrops.scheduler.task;

import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import org.jetbrains.annotations.NotNull;

public class CheckTask implements Comparable<CheckTask> {

    private final int time;
    private final TaskType type;
    private final SimpleLocation simpleLocation;

    public CheckTask(int time, TaskType type, SimpleLocation simpleLocation) {
        this.time = time;
        this.type = type;
        this.simpleLocation = simpleLocation;
    }

    public SimpleLocation getSimpleLocation() {
        return simpleLocation;
    }

    public int getTime() {
        return time;
    }

    public TaskType getType() {
        return type;
    }

    @Override
    public int compareTo(@NotNull CheckTask o) {
        return Integer.compare(this.time, o.time);
    }

    public enum TaskType {
        CROP,
        SPRINKLER,
        POT
    }
}
