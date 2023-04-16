package net.momirealms.customcrops.api.customplugin.oraxen;

import io.th0rgal.oraxen.api.events.*;
import net.momirealms.customcrops.api.customplugin.Handler;
import net.momirealms.customcrops.api.customplugin.PlatformManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class OraxenHandler extends Handler {

    public OraxenHandler(PlatformManager platformManager) {
        super(platformManager);
    }

    @EventHandler
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        platformManager.onBreakNoteBlock(event.getPlayer(), event.getBlock(), event.getMechanic().getItemID(), event);
    }

    @EventHandler
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        platformManager.onBreakTripWire(event.getPlayer(), event.getBlock(), event.getMechanic().getItemID(), event);
    }

    @EventHandler
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        Entity entity = event.getBaseEntity();
        switch (entity.getType()) {
            case ITEM_FRAME -> platformManager.onBreakItemFrame(event.getPlayer(), entity, event.getMechanic().getItemID(), event);
            case ITEM_DISPLAY -> platformManager.onBreakItemDisplay(event.getPlayer(), entity, event.getMechanic().getItemID(), event);
        }
    }

    @EventHandler
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        platformManager.onPlaceFurniture(event.getBaseEntity().getLocation().getBlock().getLocation(), event.getMechanic().getItemID());
    }

    @EventHandler
    public void onPlaceStringBlock(OraxenStringBlockPlaceEvent event) {
        platformManager.onPlaceBlock(event.getBlock().getLocation(), event.getMechanic().getItemID(), event);
    }

    @EventHandler
    public void onPlaceNoteBlock(OraxenNoteBlockPlaceEvent event) {
        platformManager.onPlaceBlock(event.getBlock().getLocation(), event.getMechanic().getItemID(), event);
    }

    @EventHandler
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        platformManager.onInteractFurniture(event.getPlayer(), event.getBaseEntity(), event.getMechanic().getItemID(), event);
    }
}
