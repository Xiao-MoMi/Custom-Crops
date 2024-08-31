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

public interface CustomCropsSection {

    static CustomCropsSection create(int sectionID) {
        return new CustomCropsSectionImpl(sectionID);
    }

    static CustomCropsSection restore(int sectionID, ConcurrentHashMap<BlockPos, CustomCropsBlockState> blocks) {
        return new CustomCropsSectionImpl(sectionID, blocks);
    }

    int getSectionID();

    @NotNull
    Optional<CustomCropsBlockState> getBlockState(BlockPos pos);

    @NotNull
    Optional<CustomCropsBlockState> removeBlockState(BlockPos pos);

    @NotNull
    Optional<CustomCropsBlockState> addBlockState(BlockPos pos, CustomCropsBlockState block);

    boolean canPrune();

    CustomCropsBlockState[] blocks();

    Map<BlockPos, CustomCropsBlockState> blockMap();
}
