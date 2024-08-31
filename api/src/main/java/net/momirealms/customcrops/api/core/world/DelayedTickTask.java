/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.world;

import org.jetbrains.annotations.NotNull;

public class DelayedTickTask implements Comparable<DelayedTickTask> {

    private static int taskID;
    private final int time;
    private final BlockPos blockPos;
    private final int id;

    public DelayedTickTask(int time, BlockPos blockPos) {
        this.time = time;
        this.blockPos = blockPos;
        this.id = taskID++;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public int getTime() {
        return time;
    }

    @Override
    public int compareTo(@NotNull DelayedTickTask o) {
        if (this.time > o.time) {
            return 1;
        } else if (this.time < o.time) {
            return -1;
        } else {
            return Integer.compare(this.id, o.id);
        }
    }
}
