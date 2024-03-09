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

package net.momirealms.customcrops.api.object.world;

import net.momirealms.customcrops.api.object.OfflineReplaceTask;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class CCChunk implements Serializable {

    @Serial
    private static final long serialVersionUID = 5300805317167684402L;

    private final ConcurrentHashMap<SimpleLocation, GrowingCrop> growingCropMap;
    private final ConcurrentHashMap<SimpleLocation, Pot> potMap;
    private final ConcurrentHashMap<SimpleLocation, Sprinkler> sprinklerMap;
    private ConcurrentHashMap<SimpleLocation, OfflineReplaceTask> replaceTaskMap;
    private final Set<SimpleLocation> greenhouseSet;
    private final Set<SimpleLocation> scarecrowSet;

    public CCChunk() {
        this.growingCropMap = new ConcurrentHashMap<>(64);
        this.potMap = new ConcurrentHashMap<>(64);
        this.sprinklerMap = new ConcurrentHashMap<>(16);
        this.greenhouseSet = Collections.synchronizedSet(new HashSet<>(64));
        this.scarecrowSet = Collections.synchronizedSet(new HashSet<>(4));
        this.replaceTaskMap = new ConcurrentHashMap<>(64);
    }

    public ConcurrentHashMap<SimpleLocation, GrowingCrop> getGrowingCropMap() {
        return growingCropMap;
    }

    public ConcurrentHashMap<SimpleLocation, Pot> getPotMap() {
        return potMap;
    }

    public ConcurrentHashMap<SimpleLocation, Sprinkler> getSprinklerMap() {
        return sprinklerMap;
    }

    public ConcurrentHashMap<SimpleLocation, OfflineReplaceTask> getReplaceTaskMap() {
        return replaceTaskMap;
    }

    public Set<SimpleLocation> getGreenhouseSet() {
        return greenhouseSet;
    }

    public Set<SimpleLocation> getScarecrowSet() {
        return scarecrowSet;
    }
}