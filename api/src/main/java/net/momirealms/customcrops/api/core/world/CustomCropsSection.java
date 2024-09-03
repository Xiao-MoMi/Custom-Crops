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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface representing a section of a chunk in the CustomCrops plugin
 */
public interface CustomCropsSection {

    /**
     * Creates a new instance of a CustomCropsSection with the specified section ID.
     *
     * @param sectionID The ID of the section to create.
     * @return A new {@link CustomCropsSection} instance.
     */
    static CustomCropsSection create(int sectionID) {
        return new CustomCropsSectionImpl(sectionID);
    }

    /**
     * Restores an existing CustomCropsSection from the provided section ID and block states.
     *
     * @param sectionID The ID of the section to restore.
     * @param blocks    A map of {@link BlockPos} to {@link CustomCropsBlockState} representing the blocks in the section.
     * @return A restored {@link CustomCropsSection} instance.
     */
    static CustomCropsSection restore(int sectionID, ConcurrentHashMap<BlockPos, CustomCropsBlockState> blocks) {
        return new CustomCropsSectionImpl(sectionID, blocks);
    }

    /**
     * Gets the ID of this section.
     *
     * @return The section ID.
     */
    int getSectionID();

    /**
     * Retrieves the block state at a specific position within this section.
     *
     * @param pos The {@link BlockPos} representing the position of the block.
     * @return An {@link Optional} containing the {@link CustomCropsBlockState} if present, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsBlockState> getBlockState(BlockPos pos);

    /**
     * Removes the block state at a specific position within this section.
     *
     * @param pos The {@link BlockPos} representing the position of the block to remove.
     * @return An {@link Optional} containing the removed {@link CustomCropsBlockState} if present, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsBlockState> removeBlockState(BlockPos pos);

    /**
     * Adds or replaces a block state at a specific position within this section.
     *
     * @param pos   The {@link BlockPos} representing the position where the block will be added.
     * @param block The {@link CustomCropsBlockState} to add.
     * @return An {@link Optional} containing the previous {@link CustomCropsBlockState} if replaced, otherwise empty.
     */
    @NotNull
    Optional<CustomCropsBlockState> addBlockState(BlockPos pos, CustomCropsBlockState block);

    /**
     * Checks if the section can be pruned (removed from memory or storage).
     *
     * @return true if the section can be pruned, false otherwise.
     */
    boolean canPrune();

    /**
     * Gets an array of all block states within this section.
     *
     * @return An array of {@link CustomCropsBlockState}.
     */
    CustomCropsBlockState[] blocks();

    /**
     * Gets a map of all block positions to their respective block states within this section.
     *
     * @return A {@link Map} of {@link BlockPos} to {@link CustomCropsBlockState}.
     */
    Map<BlockPos, CustomCropsBlockState> blockMap();
}
