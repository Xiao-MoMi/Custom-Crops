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

import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.adaptor.WorldAdaptor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public interface CustomCropsWorld<W> {

    /**
     * Create a CustomCrops world
     *
     * @param world world instance
     * @param adaptor the world adaptor
     * @return CustomCrops world
     * @param <W> world type
     */
    static <W> CustomCropsWorld<W> create(W world, WorldAdaptor<W> adaptor) {
        return new CustomCropsWorldImpl<>(world, adaptor);
    }

    /**
     * Create a new CustomCropsChunk associated with this world
     *
     * @param pos the position of the chunk
     * @return the created chunk
     */
    default CustomCropsChunk createChunk(ChunkPos pos) {
        return new CustomCropsChunkImpl(this, pos);
    }

    default CustomCropsChunk restoreChunk(
            ChunkPos pos,
            int loadedSeconds,
            long lastLoadedTime,
            ConcurrentHashMap<Integer, CustomCropsSection> loadedSections,
            PriorityQueue<DelayedTickTask> queue,
            HashSet<BlockPos> tickedBlocks
    ) {
        return new CustomCropsChunkImpl(this, pos, loadedSeconds, lastLoadedTime, loadedSections, queue, tickedBlocks);
    }

    default CustomCropsRegion createRegion(RegionPos pos) {
        return new CustomCropsRegionImpl(this, pos);
    }

    default CustomCropsRegion restoreRegion(RegionPos pos, ConcurrentHashMap<ChunkPos, byte[]> cachedChunks) {
        return new CustomCropsRegionImpl(this, pos, cachedChunks);
    }

    WorldAdaptor<W> adaptor();

    WorldExtraData extraData();

    boolean testChunkLimitation(Pos3 pos3, Class<? extends CustomCropsBlock> clazz, int amount);

    boolean doesChunkHaveBlock(Pos3 pos3, Class<? extends CustomCropsBlock> clazz);

    int getChunkBlockAmount(Pos3 pos3, Class<? extends CustomCropsBlock> clazz);

    CustomCropsChunk[] loadedChunks();

    /**
     * Get the state of the block at a certain location
     *
     * @param location location of the block state
     * @return the optional block state
     */
    @NotNull
    Optional<CustomCropsBlockState> getBlockState(Pos3 location);

    /**
     * Remove the block state from a certain location
     *
     * @param location the location of the block state
     * @return the optional removed state
     */
    @NotNull
    Optional<CustomCropsBlockState> removeBlockState(Pos3 location);

    /**
     * Add block state at the certain location
     *
     * @param location location of the state
     * @param block block state to add
     * @return the optional previous state
     */
    @NotNull
    Optional<CustomCropsBlockState> addBlockState(Pos3 location, CustomCropsBlockState block);

    /**
     * Save the world to file
     */
    void save();

    /**
     * Set if the ticking task is ongoing
     *
     * @param tick ongoing or not
     */
    void setTicking(boolean tick);

    /**
     * Get the world associated with this world
     *
     * @return Bukkit world
     */
    W world();

    World bukkitWorld();

    String worldName();

    /**
     * Get the settings of the world
     *
     * @return the setting
     */
    @NotNull
    WorldSetting setting();

    /**
     * Set the settings of the world
     *
     * @param setting setting
     */
    void setting(WorldSetting setting);

    /*
     * Chunks
     */
    boolean isChunkLoaded(ChunkPos pos);

    /**
     * Get loaded chunk from cache
     *
     * @param chunkPos the position of the chunk
     * @return the optional loaded chunk
     */
    @NotNull
    Optional<CustomCropsChunk> getLoadedChunk(ChunkPos chunkPos);

    /**
     * Get chunk from cache or file
     *
     * @param chunkPos the position of the chunk
     * @return the optional chunk
     */
    @NotNull
    Optional<CustomCropsChunk> getChunk(ChunkPos chunkPos);

    /**
     * Get chunk from cache or file, create if not found
     *
     * @param chunkPos the position of the chunk
     * @return the chunk
     */
    @NotNull
    CustomCropsChunk getOrCreateChunk(ChunkPos chunkPos);

    /**
     * Check if a region is loaded
     *
     * @param regionPos the position of the region
     * @return loaded or not
     */
    boolean isRegionLoaded(RegionPos regionPos);

    /**
     * Get the loaded region, empty if not loaded
     *
     * @param regionPos position of the region
     * @return the optional loaded region
     */
    @NotNull
    Optional<CustomCropsRegion> getLoadedRegion(RegionPos regionPos);

    /**
     * Get the region from cache or file
     *
     * @param regionPos position of the region
     * @return the optional region
     */
    @NotNull
    Optional<CustomCropsRegion> getRegion(RegionPos regionPos);

    /**
     * Get the region from cache or file, create if not found
     *
     * @param regionPos position of the region
     * @return the region
     */
    @NotNull
    CustomCropsRegion getOrCreateRegion(RegionPos regionPos);
}
