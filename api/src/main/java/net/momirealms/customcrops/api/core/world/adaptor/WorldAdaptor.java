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

public interface WorldAdaptor<W> extends Comparable<WorldAdaptor<W>> {

    int BUKKIT_WORLD_PRIORITY = 100;
    int SLIME_WORLD_PRIORITY = 200;

    WorldExtraData loadExtraData(W world);

    void saveExtraData(CustomCropsWorld<W> world);

    /**
     * Load the region from file or cache
     */
    @Nullable
    CustomCropsRegion loadRegion(CustomCropsWorld<W> world, RegionPos pos, boolean createIfNotExist);

    /**
     * Load the chunk from file or cache
     */
    @Nullable
    CustomCropsChunk loadChunk(CustomCropsWorld<W> world, ChunkPos pos, boolean createIfNotExist);

    /**
     * Unload the region to file or cache
     */
    void saveRegion(CustomCropsWorld<W> world, CustomCropsRegion region);

    void saveChunk(CustomCropsWorld<W> world, CustomCropsChunk chunk);

    String getName(W world);

    @Nullable
    W getWorld(String worldName);

    CustomCropsWorld<W> adapt(Object world);

    long getWorldFullTime(W world);

    int priority();

}
