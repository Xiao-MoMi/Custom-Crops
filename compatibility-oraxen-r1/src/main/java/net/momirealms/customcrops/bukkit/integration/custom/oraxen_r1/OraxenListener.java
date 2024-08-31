package net.momirealms.customcrops.bukkit.integration.custom.oraxen_r1;

import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurniturePlaceEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockInteractEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockPlaceEvent;
import net.momirealms.customcrops.api.core.AbstractCustomEventListener;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import org.bukkit.event.EventHandler;

public class OraxenListener extends AbstractCustomEventListener {

    public OraxenListener(AbstractItemManager itemManager) {
        super(itemManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        itemManager.handlePlayerInteractFurniture(
                event.getPlayer(),
                event.getBaseEntity().getLocation(), event.getMechanic().getItemID(),
                event.getHand(), event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(OraxenNoteBlockInteractEvent event) {
        itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getMechanic().getItemID(), event.getBlockFace(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(OraxenStringBlockInteractEvent event) {
        itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                event.getBlock(),
                event.getMechanic().getItemID(), event.getBlockFace(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBaseEntity().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(OraxenNoteBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBlock().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(OraxenStringBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBlock().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBaseEntity().getLocation(),
                event.getMechanic().getItemID(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(OraxenNoteBlockPlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(OraxenStringBlockPlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }
}
