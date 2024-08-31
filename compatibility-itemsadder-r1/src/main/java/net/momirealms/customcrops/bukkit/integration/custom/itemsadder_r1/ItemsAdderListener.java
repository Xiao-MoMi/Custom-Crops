package net.momirealms.customcrops.bukkit.integration.custom.itemsadder_r1;

import dev.lone.itemsadder.api.Events.*;
import net.momirealms.customcrops.api.core.AbstractCustomEventListener;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import net.momirealms.customcrops.api.util.FakeCancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;

public class ItemsAdderListener extends AbstractCustomEventListener {

    public ItemsAdderListener(AbstractItemManager itemManager) {
        super(itemManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractFurniture(FurnitureInteractEvent event) {
        itemManager.handlePlayerInteractFurniture(
                event.getPlayer(),
                event.getBukkitEntity().getLocation(), event.getNamespacedID(),
                EquipmentSlot.HAND, event.getPlayer().getInventory().getItemInMainHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCustomBlock(CustomBlockInteractEvent event) {
        itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                event.getBlockClicked(),
                event.getNamespacedID(), event.getBlockFace(),
                event.getHand(),
                event.getItem(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakFurniture(FurnitureBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBukkitEntity().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getNamespacedID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCustomBlock(CustomBlockBreakEvent event) {
        itemManager.handlePlayerBreak(
                event.getPlayer(),
                event.getBlock().getLocation(), event.getPlayer().getInventory().getItemInMainHand(), event.getNamespacedID(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCustomBlock(CustomBlockPlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getNamespacedID(),
                EquipmentSlot.HAND,
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBukkitEntity().getLocation(),
                event.getNamespacedID(),
                EquipmentSlot.HAND,
                event.getPlayer().getInventory().getItemInMainHand(),
                new FakeCancellable()
        );
    }
}
