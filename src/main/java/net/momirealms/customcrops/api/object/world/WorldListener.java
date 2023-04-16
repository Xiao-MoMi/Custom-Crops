package net.momirealms.customcrops.api.object.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {

    private final WorldDataManager worldManager;

    public WorldListener(WorldDataManager worldManager) {
        this.worldManager = worldManager;
    }

    @EventHandler
    public void onChunkLoad(WorldLoadEvent event) {
        worldManager.loadWorld(event.getWorld());
    }

    @EventHandler
    public void onChunkUnload(WorldUnloadEvent event) {
        worldManager.unloadWorld(event.getWorld());
    }
}
