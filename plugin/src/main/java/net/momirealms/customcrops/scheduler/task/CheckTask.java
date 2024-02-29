/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.scheduler.task;

import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import org.jetbrains.annotations.NotNull;

public class CheckTask implements Comparable<CheckTask> {

    private int time;
    private TaskType type;
    private SimpleLocation simpleLocation;

    private CheckTask() {
    }

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
