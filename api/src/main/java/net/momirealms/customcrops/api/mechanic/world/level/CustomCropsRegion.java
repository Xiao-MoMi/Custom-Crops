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

package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.RegionPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CustomCropsRegion {

    /**
     * Get the CustomCrops world associated with the region
     *
     * @return CustomCrops world
     */
    CustomCropsWorld getCustomCropsWorld();

    /**
     * Get the cached chunk
     *
     * @param pos chunk position
     * @return cached chunk in bytes
     */
    byte @Nullable [] getChunkBytes(ChunkPos pos);

    /**
     * Get the position of the region
     *
     * @return the position of the region
     */
    RegionPos getRegionPos();

    /**
     * Remove a chunk by the position
     *
     * @param pos the position of the chunk
     */
    void removeChunk(ChunkPos pos);

    /**
     * Put a chunk's data to cache
     *
     * @param pos the position of the chunk
     * @param data the serialized data
     */
    void saveChunk(ChunkPos pos, byte[] data);

    /**
     * Get the data to save
     *
     * @return the data to save
     */
    Map<ChunkPos, byte[]> getRegionDataToSave();

    /**
     * If the region can be pruned or not
     */
    boolean canPrune();
}
