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

package net.momirealms.customcrops.api.core.world.adaptor;

import net.momirealms.customcrops.api.core.world.*;
import org.jetbrains.annotations.Nullable;

/**
 * Interface defining methods for adapting different types of worlds (e.g., Bukkit, Slime) for use with CustomCrops.
 * This adaptor provides methods to load and save regions and chunks, handle world-specific data, and interact with
 * various world implementations.
 *
 * @param <W> The type of the world that this adaptor supports.
 */
public interface WorldAdaptor<W> extends Comparable<WorldAdaptor<W>> {

    int BUKKIT_WORLD_PRIORITY = 100;
    int SLIME_WORLD_PRIORITY = 200;
    /**
     * Loads extra data associated with the given world.
     *
     * @param world The world to load data for.
     * @return The loaded {@link WorldExtraData} containing extra world-specific information.
     */
    WorldExtraData loadExtraData(W world);

    /**
     * Saves extra data for the given CustomCrops world instance.
     *
     * @param world The CustomCrops world instance whose extra data is to be saved.
     */
    void saveExtraData(CustomCropsWorld<W> world);

    /**
     * Loads a region from the file or cache. Creates a new region if it doesn't exist and createIfNotExist is true.
     *
     * @param world            The CustomCrops world instance to which the region belongs.
     * @param pos              The position of the region to be loaded.
     * @param createIfNotExist If true, creates the region if it does not exist.
     * @return The loaded {@link CustomCropsRegion}, or null if the region could not be loaded and createIfNotExist is false.
     */
    @Nullable
    CustomCropsRegion loadRegion(CustomCropsWorld<W> world, RegionPos pos, boolean createIfNotExist);

    /**
     * Loads a chunk from the file or cache. Creates a new chunk if it doesn't exist and createIfNotExist is true.
     *
     * @param world            The CustomCrops world instance to which the chunk belongs.
     * @param pos              The position of the chunk to be loaded.
     * @param createIfNotExist If true, creates the chunk if it does not exist.
     * @return The loaded {@link CustomCropsChunk}, or null if the chunk could not be loaded and createIfNotExist is false.
     */
    @Nullable
    CustomCropsChunk loadChunk(CustomCropsWorld<W> world, ChunkPos pos, boolean createIfNotExist);

    /**
     * Saves the specified region to a file or cache.
     *
     * @param world  The CustomCrops world instance to which the region belongs.
     * @param region The region to be saved.
     */
    void saveRegion(CustomCropsWorld<W> world, CustomCropsRegion region);

    /**
     * Saves the specified chunk to a file or cache.
     *
     * @param world The CustomCrops world instance to which the chunk belongs.
     * @param chunk The chunk to be saved.
     */
    void saveChunk(CustomCropsWorld<W> world, CustomCropsChunk chunk);

    /**
     * Retrieves the name of the given world.
     *
     * @param world The world instance.
     * @return The name of the world.
     */
    String getName(W world);

    /**
     * Gets the world instance by its name.
     *
     * @param worldName The name of the world to retrieve.
     * @return The world instance, or null if no world with the given name is found.
     */
    @Nullable
    W getWorld(String worldName);

    /**
     * Adapts the given object to a CustomCropsWorld instance if possible.
     *
     * @param world The object to adapt.
     * @return The adapted {@link CustomCropsWorld} instance.
     */
    CustomCropsWorld<W> adapt(Object world);

    /**
     * Gets the priority of this world adaptor. Adaptors with lower priority values are considered before those with higher values.
     *
     * @return The priority value of this adaptor.
     */
    int priority();
}
