package net.momirealms.customcrops.api.customplugin.itemsadder;

import dev.lone.itemsadder.api.Events.*;
import net.momirealms.customcrops.api.customplugin.Handler;
import net.momirealms.customcrops.api.customplugin.PlatformManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class ItemsAdderHandler extends Handler {

    public ItemsAdderHandler(PlatformManager platformManager) {
        super(platformManager);
    }

    @EventHandler
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        Block block = event.getBlock();
        switch (block.getType()) {
            case NOTE_BLOCK -> platformManager.onBreakNoteBlock(event.getPlayer(), event.getBlock(), event.getNamespacedID(), event);
            case TRIPWIRE -> platformManager.onBreakTripWire(event.getPlayer(), event.getBlock(), event.getNamespacedID(), event);
        }
    }

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event) {
        Entity entity = event.getBukkitEntity();
        switch (entity.getType()) {
            case ITEM_FRAME -> platformManager.onBreakItemFrame(event.getPlayer(), entity, event.getNamespacedID(), event);
            case ITEM_DISPLAY -> platformManager.onBreakItemDisplay(event.getPlayer(), entity, event.getNamespacedID(), event);
        }
    }

    @EventHandler
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        platformManager.onPlaceFurniture(event.getBukkitEntity().getLocation().getBlock().getLocation(), event.getNamespacedID());
    }

    @EventHandler
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        platformManager.onPlaceBlock(event.getBlock().getLocation(), event.getNamespacedID(), event);
    }

    @EventHandler
    public void onInteractFurniture(FurnitureInteractEvent event) {
        platformManager.onInteractFurniture(event.getPlayer(), event.getBukkitEntity(), event.getNamespacedID(), event);
    }
}
