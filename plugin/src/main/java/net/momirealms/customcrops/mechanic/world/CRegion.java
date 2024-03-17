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

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.RegionPos;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsRegion;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CRegion implements CustomCropsRegion {

    private final CWorld cWorld;
    private final RegionPos regionPos;
    private final ConcurrentHashMap<ChunkPos, byte[]> cachedChunkBytes;

    public CRegion(CWorld cWorld, RegionPos regionPos) {
        this.cWorld = cWorld;
        this.regionPos = regionPos;
        this.cachedChunkBytes = new ConcurrentHashMap<>();
    }

    public CRegion(CWorld cWorld, RegionPos regionPos, ConcurrentHashMap<ChunkPos, byte[]> cachedChunkBytes) {
        this.cWorld = cWorld;
        this.regionPos = regionPos;
        this.cachedChunkBytes = cachedChunkBytes;
    }

    @Override
    public CustomCropsWorld getCustomCropsWorld() {
        return cWorld;
    }

    @Nullable
    @Override
    public byte[] getChunkBytes(ChunkPos pos) {
        return cachedChunkBytes.get(pos);
    }

    @Override
    public RegionPos getRegionPos() {
        return regionPos;
    }

    @Override
    public void removeChunk(ChunkPos pos) {
        cachedChunkBytes.remove(pos);
    }

    @Override
    public void saveChunk(ChunkPos pos, byte[] data) {
        cachedChunkBytes.put(pos, data);
    }

    @Override
    public Map<ChunkPos, byte[]> getRegionDataToSave() {
        return new HashMap<>(cachedChunkBytes);
    }

    @Override
    public boolean canPrune() {
        return cachedChunkBytes.size() == 0;
    }
}
