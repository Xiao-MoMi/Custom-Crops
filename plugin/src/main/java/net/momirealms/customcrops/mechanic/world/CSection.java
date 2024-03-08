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

package net.momirealms.customcrops.mechanic.world;

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CSection implements CustomCropsSection {

    private final int sectionID;
    private final ConcurrentHashMap<ChunkPos, CustomCropsBlock> blocks;

    public CSection(int sectionID) {
        this.sectionID = sectionID;
        this.blocks = new ConcurrentHashMap<>();
    }

    public CSection(int sectionID, ConcurrentHashMap<ChunkPos, CustomCropsBlock> blocks) {
        this.blocks = blocks;
        this.sectionID = sectionID;
    }

    @Override
    public int getSectionID() {
        return sectionID;
    }

    @Override
    public CustomCropsBlock getBlockAt(ChunkPos pos) {
        return blocks.get(pos);
    }

    @Override
    public CustomCropsBlock removeBlockAt(ChunkPos pos) {
        return blocks.remove(pos);
    }

    @Override
    public CustomCropsBlock addBlockAt(ChunkPos pos, CustomCropsBlock block) {
        return blocks.put(pos, block);
    }

    @Override
    public boolean canPrune() {
        return blocks.size() == 0;
    }

    @Override
    public CustomCropsBlock[] getBlocks() {
        return blocks.values().toArray(new CustomCropsBlock[0]);
    }

    @Override
    public Map<ChunkPos, CustomCropsBlock> getBlockMap() {
        return blocks;
    }
}
