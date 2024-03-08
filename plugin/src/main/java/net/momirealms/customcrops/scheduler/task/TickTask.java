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

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import org.jetbrains.annotations.NotNull;

public class TickTask implements Comparable<TickTask> {

    private static int taskID;
    private final int time;
    private final ChunkPos chunkPos;
    private final int id;

    public TickTask(int time, ChunkPos chunkPos) {
        this.time = time;
        this.chunkPos = chunkPos;
        this.id = taskID++;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public int getTime() {
        return time;
    }

    @Override
    public int compareTo(@NotNull TickTask o) {
        if (this.time > o.time) {
            return 1;
        } else if (this.time < o.time) {
            return -1;
        } else {
            return Integer.compare(this.id, o.id);
        }
    }
}
