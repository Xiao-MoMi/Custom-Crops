package net.momirealms.customcrops.gui;

import net.momirealms.customcrops.api.event.PotInteractEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GUIManager implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onInteractPot(PotInteractEvent event) {
        if (event.getItemInHand().getType() != Material.AIR)
            return;

    }
}
