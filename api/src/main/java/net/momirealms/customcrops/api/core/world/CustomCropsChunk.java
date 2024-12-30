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

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;

/**
 * Interface representing a chunk in the CustomCrops plugin
 */
public interface CustomCropsChunk {

    /**
     * Sets whether the chunk can be force loaded.
     * If a chunk is force loaded, it cannot be unloaded unless force loading is set to false.
     * This prevents CustomCrops from unloading the chunk during a {@link org.bukkit.event.world.ChunkUnloadEvent}.
     *
     * Note: This value is not persistently stored. To force a chunk to stay loaded persistently,
     * use {@link org.bukkit.World#setChunkForceLoaded(int, int, boolean)}.
     *
     * @param forceLoad Whether the chunk should be force loaded.
     */
    void setForceLoaded(boolean forceLoad);

    /**
     * Checks if the chunk is force loaded.
     *
     * @return true if the chunk is force loaded, false otherwise.
     */
    boolean isForceLoaded();

    /**
     * Loads the chunk into the cache to participate in the plugin's mechanisms.
     *
     * @param loadBukkitChunk Whether to temporarily load the Bukkit chunk if it is not already loaded.
     */
    void load(boolean loadBukkitChunk);

    /**
     * Unloads the chunk, with an option for a lazy unload.
     * Lazy unloading delays the unload, which is useful if the chunk is likely to be loaded again soon.
     *
     * @param lazy Whether to delay the unload (lazy unload).
     */
    void unload(boolean lazy);

    /**
     * Unloads the chunk if it is marked as lazy.
     */
    void unloadLazy();

    /**
     * Checks if the chunk is marked as lazy.
     *
     * @return true if the chunk is lazy, false otherwise.
     */
    boolean isLazy();

    /**
     * Checks if the chunk is currently loaded.
     *
     * @return true if the chunk is loaded, false otherwise.
     */
    boolean isLoaded();

    /**
     * Gets the world associated with this chunk.
     *
     * @return The {@link CustomCropsWorld} instance.
     */
    CustomCropsWorld<?> getWorld();

    /**
     * Gets the position of this chunk.
     *
     * @return The {@link ChunkPos} representing the chunk's position.
     */
    ChunkPos chunkPos();

    /**
     * Executes a timer task associated with this chunk.
     */
    void timer();

    /**
     * Gets the time in seconds since the chunk was unloaded.
     * This value increases if the chunk is in a lazy state.
     *
     * @return The unloaded time in seconds.
     */
    int lazySeconds();

    /**
     * Sets the time in seconds since the chunk was unloaded.
     *
     * @param lazySeconds The unloaded time to set.
     */
    void lazySeconds(int lazySeconds);

    /**
     * Gets the last time the chunk was loaded.
     *
     * @return The timestamp of the last loaded time.
     */
    long lastLoadedTime();

    /**
     * Updates the last loaded time to the current time.
     */
    void updateLastUnloadTime();

    /**
     * Gets the time in milliseconds since the chunk was loaded.
     *
     * @return The loaded time in milliseconds.
     */
    int loadedMilliSeconds();

    int loadedSeconds();

    /**
     * Retrieves the custom crop block state at a specific location.
     *
     * @param location The location to check.
     * @return An {@link Optional} containing the {@link CustomCropsBlockState} if present, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsBlockState> getBlockState(Pos3 location);

    /**
     * Removes any custom crop block state at a specific location.
     *
     * @param location The location from which to remove the block state.
     * @return An {@link Optional} containing the removed {@link CustomCropsBlockState} if present, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsBlockState> removeBlockState(Pos3 location);

    /**
     * Adds a custom crop block state at a specific location.
     *
     * @param location The location to add the block state.
     * @param block The custom crop block state to add.
     * @return An {@link Optional} containing the previous {@link CustomCropsBlockState} if replaced, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsBlockState> addBlockState(Pos3 location, CustomCropsBlockState block);

    /**
     * Gets a stream of custom crop sections that need to be saved.
     *
     * @return A {@link Stream} of {@link CustomCropsSection} to save.
     */
    @NotNull
    Stream<CustomCropsSection> sectionsToSave();

    /**
     * Retrieves a loaded section by its ID.
     *
     * @param sectionID The ID of the section to retrieve.
     * @return An {@link Optional} containing the {@link CustomCropsSection} if loaded, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsSection> getLoadedSection(int sectionID);

    /**
     * Retrieves a section by its ID, loading it if necessary.
     *
     * @param sectionID The ID of the section to retrieve.
     * @return The {@link CustomCropsSection} instance.
     */
    CustomCropsSection getSection(int sectionID);

    /**
     * Retrieves all sections within this chunk.
     *
     * @return An array of {@link CustomCropsSection}.
     */
    CustomCropsSection[] sections();

    /**
     * Removes a section by its ID.
     *
     * @param sectionID The ID of the section to remove.
     * @return An {@link Optional} containing the removed {@link CustomCropsSection} if present, otherwise empty.
     */
    Optional<CustomCropsSection> removeSection(int sectionID);

    /**
     * Checks if the chunk can be pruned (removed from memory or storage).
     *
     * @return true if the chunk can be pruned, false otherwise.
     */
    boolean canPrune();

    /**
     * Checks if offline tasks have been notified for this chunk.
     *
     * @return true if offline tasks are notified, false otherwise.
     */
    boolean isOfflineTaskNotified();

    /**
     * notify offline tasks
     */
    void notifyOfflineTask();

    /**
     * Gets the queue of delayed tick tasks for this chunk.
     *
     * @return A {@link PriorityQueue} of {@link DelayedTickTask}.
     */
    PriorityBlockingQueue<DelayedTickTask> tickTaskQueue();

    /**
     * Gets the set of blocks that have been ticked in one tick cycle within this chunk.
     *
     * @return A {@link Set} of {@link BlockPos} representing ticked blocks.
     */
    Set<BlockPos> tickedBlocks();
}
