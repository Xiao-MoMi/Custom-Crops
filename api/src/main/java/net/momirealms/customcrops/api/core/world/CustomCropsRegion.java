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

import java.util.Map;

/**
 * Interface representing a region in the CustomCrops plugin
 */
public interface CustomCropsRegion {

    /**
     * Checks if the region is currently loaded.
     *
     * @return true if the region is loaded, false otherwise.
     */
    boolean isLoaded();

    /**
     * Unloads the region, freeing up resources.
     */
    void unload();

    /**
     * Loads the region into memory, preparing it for operations.
     */
    void load();

    /**
     * Gets the world associated with this region.
     *
     * @return The {@link CustomCropsWorld} instance representing the world.
     */
    @NotNull
    CustomCropsWorld<?> getWorld();

    /**
     * Retrieves the cached data of a chunk within this region by its position.
     *
     * @param pos The {@link ChunkPos} representing the position of the chunk.
     * @return A byte array representing the cached data of the chunk, or null if no data is cached.
     */
    byte[] getCachedChunkBytes(ChunkPos pos);

    /**
     * Gets the position of this region.
     *
     * @return The {@link RegionPos} representing the region's position.
     */
    @NotNull
    RegionPos regionPos();

    /**
     * Removes the cached data of a chunk from this region by its position.
     *
     * @param pos The {@link ChunkPos} representing the position of the chunk.
     * @return true if the cached data was removed successfully, false otherwise.
     */
    boolean removeCachedChunk(ChunkPos pos);

    /**
     * Caches the data of a chunk within this region at the specified position.
     *
     * @param pos  The {@link ChunkPos} representing the position of the chunk.
     * @param data A byte array representing the data to cache for the chunk.
     */
    void setCachedChunk(ChunkPos pos, byte[] data);

    /**
     * Retrieves a map of all chunks and their data that need to be saved.
     *
     * @return A {@link Map} where the key is {@link ChunkPos} and the value is a byte array of the chunk data.
     */
    Map<ChunkPos, byte[]> dataToSave();

    /**
     * Checks if the region can be pruned (removed from memory or storage).
     *
     * @return true if the region can be pruned, false otherwise.
     */
    boolean canPrune();
}
