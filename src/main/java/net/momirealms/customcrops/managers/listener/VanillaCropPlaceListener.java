package net.momirealms.customcrops.managers.listener;

import net.momirealms.customcrops.config.MainConfig;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class VanillaCropPlaceListener implements Listener {

    @EventHandler
    public void onPlant(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Material type = event.getBlockPlaced().getType();
        for (Material vanillaCrop : MainConfig.preventPlantVanillaArray) {
            if (type == vanillaCrop) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
