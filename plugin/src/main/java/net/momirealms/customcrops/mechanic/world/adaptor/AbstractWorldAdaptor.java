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

package net.momirealms.customcrops.mechanic.world.adaptor;

import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import org.bukkit.event.Listener;

public abstract class AbstractWorldAdaptor implements Listener {

    public static final int version = 1;
    protected WorldManager worldManager;

    public AbstractWorldAdaptor(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public abstract void unload(CustomCropsWorld customCropsWorld);

    public abstract void init(CustomCropsWorld customCropsWorld);

    public abstract void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate);

    public abstract void unloadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate);

    public abstract void saveDynamicData(CustomCropsWorld ccWorld, CustomCropsChunk chunk);
}
