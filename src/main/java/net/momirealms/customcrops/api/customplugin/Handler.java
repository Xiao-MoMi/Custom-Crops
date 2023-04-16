package net.momirealms.customcrops.api.customplugin;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Handler extends Function implements Listener {

    protected PlatformManager platformManager;

    public Handler(PlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, CustomCrops.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        platformManager.onInteractBlock(event);
    }
}