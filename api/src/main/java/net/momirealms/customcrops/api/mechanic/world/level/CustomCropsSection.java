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

import net.momirealms.customcrops.api.mechanic.world.BlockPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CustomCropsSection {

    /**
     * Get the section ID
     *
     * @return section ID
     */
    int getSectionID();

    /**
     * Get block at a certain position
     *
     * @param pos block position
     * @return the block
     */
    @Nullable
    CustomCropsBlock getBlockAt(BlockPos pos);

    /**
     * Remove a block by a certain position
     *
     * @param pos block position
     * @return the removed block
     */
    @Nullable
    CustomCropsBlock removeBlockAt(BlockPos pos);

    /**
     * Add block at a certain position
     *
     * @param pos block position
     * @param block the new block
     * @return the previous block
     */
    @Nullable
    CustomCropsBlock addBlockAt(BlockPos pos, CustomCropsBlock block);

    /**
     * If the section can be pruned or not
     */
    boolean canPrune();

    /**
     * Get the blocks in this section
     *
     * @return blocks
     */
    CustomCropsBlock[] getBlocks();

    /**
     * Get the block map
     *
     * @return block map
     */
    Map<BlockPos, CustomCropsBlock> getBlockMap();
}
