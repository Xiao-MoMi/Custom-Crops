package net.momirealms.customcrops.api.object.world;

import com.infernalsuite.aswm.api.events.LoadSlimeWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SlimeWorldListener implements Listener {

    private final WorldDataManager worldDataManager;

    public SlimeWorldListener(WorldDataManager worldDataManager) {
        this.worldDataManager = worldDataManager;
    }

    @EventHandler
    public void onWorldLoad(LoadSlimeWorldEvent event) {
        World world = Bukkit.getWorld(event.getSlimeWorld().getName());
        if (world != null) {
            worldDataManager.loadWorld(world);
        }
    }
}
