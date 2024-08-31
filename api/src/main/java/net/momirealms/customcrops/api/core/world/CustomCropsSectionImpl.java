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

public class CustomCropsSectionImpl implements CustomCropsSection {

    private final int sectionID;
    private final ConcurrentHashMap<BlockPos, CustomCropsBlockState> blocks;

    protected CustomCropsSectionImpl(int sectionID) {
        this.sectionID = sectionID;
        this.blocks = new ConcurrentHashMap<>();
    }

    protected CustomCropsSectionImpl(int sectionID, ConcurrentHashMap<BlockPos, CustomCropsBlockState> blocks) {
        this.sectionID = sectionID;
        this.blocks = blocks;
    }

    @Override
    public int getSectionID() {
        return sectionID;
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> getBlockState(BlockPos pos) {
        return Optional.ofNullable(blocks.get(pos));
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> removeBlockState(BlockPos pos) {
        return Optional.ofNullable(blocks.remove(pos));
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> addBlockState(BlockPos pos, CustomCropsBlockState block) {
        return Optional.ofNullable(blocks.put(pos, block));
    }

    @Override
    public boolean canPrune() {
        return blocks.isEmpty();
    }

    @Override
    public CustomCropsBlockState[] blocks() {
        return blocks.values().toArray(new CustomCropsBlockState[0]);
    }

    @Override
    public Map<BlockPos, CustomCropsBlockState> blockMap() {
        return blocks;
    }
}
