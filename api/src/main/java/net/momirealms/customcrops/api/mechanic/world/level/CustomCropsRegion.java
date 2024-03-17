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

import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.RegionPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CustomCropsRegion {

    CustomCropsWorld getCustomCropsWorld();

    byte @Nullable [] getChunkBytes(ChunkPos pos);

    RegionPos getRegionPos();

    void removeChunk(ChunkPos pos);

    void saveChunk(ChunkPos pos, byte[] data);

    Map<ChunkPos, byte[]> getRegionDataToSave();

    boolean canPrune();
}
