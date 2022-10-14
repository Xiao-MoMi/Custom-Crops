package net.momirealms.customcrops.managers.listener;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerModeListener implements Listener {

    @EventHandler
    public void onModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;
        Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, () -> {
            event.getPlayer().updateInventory();
        }, 1);
    }
}
