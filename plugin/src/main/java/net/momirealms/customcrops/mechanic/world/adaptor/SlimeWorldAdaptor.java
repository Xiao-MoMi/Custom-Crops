package net.momirealms.customcrops.mechanic.world.adaptor;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.events.LoadSlimeWorldEvent;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class SlimeWorldAdaptor extends AbstractWorldAdaptor {

    private final SlimePlugin slimePlugin;

    public SlimeWorldAdaptor(WorldManager worldManager) {
        super(worldManager);
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }

    @EventHandler (ignoreCancelled = true)
    public void onSlimeWorldLoad(LoadSlimeWorldEvent event) {
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {

    }

    @EventHandler (ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
    }


    public void unload(CustomCropsWorld customCropsWorld) {

    }

    public void init(CustomCropsWorld customCropsWorld) {

    }

    @Override
    public void loadAllData(CustomCropsWorld customCropsWorld) {

    }

    @Override
    public void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {

    }

    @Override
    public void unloadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {

    }

    private boolean isSlimeWorld(String name) {
        return slimePlugin.getWorld(name) != null;
    }
}
