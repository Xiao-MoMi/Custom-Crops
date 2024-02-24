package net.momirealms.customcrops.mechanic.world.adaptor;

import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import org.bukkit.event.Listener;

public abstract class AbstractWorldAdaptor implements Listener {

    protected WorldManager worldManager;

    public AbstractWorldAdaptor(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public abstract void unload(CustomCropsWorld customCropsWorld);

    public abstract void init(CustomCropsWorld customCropsWorld);

    public abstract void loadAllData(CustomCropsWorld customCropsWorld);

    public abstract void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate);

    public abstract void unloadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate);
}
