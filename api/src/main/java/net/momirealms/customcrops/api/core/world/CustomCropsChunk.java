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

import java.util.Collection;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

public interface CustomCropsChunk {

    /**
     * Set if the chunk can be force loaded.
     * If a chunk is force loaded, no one can unload it unless you set force load to false.
     * This can prevent CustomCrops from unloading the chunks on {@link org.bukkit.event.world.ChunkUnloadEvent}
     *
     * This value will not be persistently stored. Please use {@link org.bukkit.World#setChunkForceLoaded(int, int, boolean)}
     * if you want to force a chunk loaded.
     *
     * @param forceLoad force loaded
     */
    void setForceLoaded(boolean forceLoad);

    /**
     * Indicates whether the chunk is force loaded
     *
     * @return force loaded or not
     */
    boolean isForceLoaded();

    /**
     * Loads the chunk to cache and participate in the mechanism of the plugin.
     *
     * @param loadBukkitChunk whether to load Bukkit chunks temporarily if it's not loaded
     */
    void load(boolean loadBukkitChunk);

    /**
     * Unloads the chunk. Lazy refer to those chunks that will be delayed for unloading.
     * Recently unloaded chunks are likely to be loaded again soon.
     *
     * @param lazy delay unload or not
     */
    void unload(boolean lazy);

    /**
     * Unloads the chunk if it is a lazy chunk
     */
    void unloadLazy();

    /**
     * Indicates whether the chunk is in lazy state
     *
     * @return lazy or not
     */
    boolean isLazy();

    /**
     * Indicates whether the chunk is loaded
     *
     * @return loaded or not
     */
    boolean isLoaded();

    /**
     * Get the world associated with the chunk
     *
     * @return CustomCrops world
     */
    CustomCropsWorld<?> getWorld();

    /**
     * Get the position of the chunk
     *
     * @return chunk position
     */
    ChunkPos chunkPos();

    /**
     * Do second timer
     */
    void timer();

    /**
     * Get the unloaded time in seconds
     * This value would increase if the chunk is lazy
     *
     * @return the unloaded time
     */
    int unloadedSeconds();

    /**
     * Set the unloaded seconds
     *
     * @param unloadedSeconds unloadedSeconds
     */
    void unloadedSeconds(int unloadedSeconds);

    /**
     * Get the last loaded time
     *
     * @return last loaded time
     */
    long lastLoadedTime();

    /**
     * Set the last loaded time to current time
     */
    void updateLastLoadedTime();

    /**
     * Get the loaded time in seconds
     *
     * @return loaded time
     */
    int loadedMilliSeconds();

    /**
     * Get block data at a certain location
     *
     * @param location location
     * @return block data
     */
    @NotNull
    Optional<CustomCropsBlockState> getBlockState(Pos3 location);

    /**
     * Remove any block data from a certain location
     *
     * @param location location
     * @return block data
     */
    @NotNull
    Optional<CustomCropsBlockState> removeBlockState(Pos3 location);

    /**
     * Add a custom block data at a certain location
     *
     * @param block block to add
     * @return the previous block data
     */
    @NotNull
    Optional<CustomCropsBlockState> addBlockState(Pos3 location, CustomCropsBlockState block);

    /**
     * Get CustomCrops sections
     *
     * @return sections
     */
    @NotNull
    Stream<CustomCropsSection> sectionsToSave();

    /**
     * Get section by ID
     *
     * @param sectionID id
     * @return section
     */
    @NotNull
    Optional<CustomCropsSection> getLoadedSection(int sectionID);

    CustomCropsSection getSection(int sectionID);

    Collection<CustomCropsSection> sections();

    Optional<CustomCropsSection> removeSection(int sectionID);

    void resetUnloadedSeconds();

    boolean canPrune();

    boolean isOfflineTaskNotified();

    PriorityQueue<DelayedTickTask> tickTaskQueue();

    Set<BlockPos> tickedBlocks();
}
