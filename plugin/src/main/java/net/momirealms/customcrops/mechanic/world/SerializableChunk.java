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

package net.momirealms.customcrops.mechanic.world;

import com.flowpowered.nbt.CompoundTag;

import java.util.List;

public class SerializableChunk {

    private final int x;
    private final int z;
    private final int loadedSeconds;
    private final long lastLoadedTime;
    private final List<SerializableSection> sections;
    private final List<CompoundTag> queuedTasks;

    public SerializableChunk(int x, int z, int loadedSeconds, long lastLoadedTime, List<SerializableSection> sections, List<CompoundTag> queuedTasks) {
        this.x = x;
        this.z = z;
        this.lastLoadedTime = lastLoadedTime;
        this.loadedSeconds = loadedSeconds;
        this.sections = sections;
        this.queuedTasks = queuedTasks;
    }

    public int getLoadedSeconds() {
        return loadedSeconds;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long getLastLoadedTime() {
        return lastLoadedTime;
    }

    public List<SerializableSection> getSections() {
        return sections;
    }

    public List<CompoundTag> getQueuedTasks() {
        return queuedTasks;
    }
}
