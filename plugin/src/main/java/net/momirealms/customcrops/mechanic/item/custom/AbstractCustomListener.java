package net.momirealms.customcrops.mechanic.item.custom;

import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCustomListener implements Listener {

    protected ItemManagerImpl itemManager;

    public AbstractCustomListener(ItemManagerImpl itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        this.itemManager.handlePlayerInteractBlock(
                player,
                player.getInventory().getItemInMainHand(),
                event.getClickedBlock(),
                event.getBlockFace(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractAir(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        Player player = event.getPlayer();
        this.itemManager.handlePlayerInteractAir(
                player,
                player.getInventory().getItemInMainHand(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        this.itemManager.handlePlayerBreakBlock(
                player,
                player.getInventory().getItemInMainHand(),
                event.getBlock(),
                event
        );
    }

    /**
     * CustomCrops only reads necessary data from the event and would not modify it
     */
    public void onBreakFurniture(Player player, Location location, String id, Cancellable event) {
        this.itemManager.handlePlayerBreakFurniture(player, location, id, event);
    }

    /**
     * CustomCrops only reads necessary data from the event and would not modify it
     */
    public void onPlaceFurniture(Player player, Location location, String id, Cancellable event) {
        this.itemManager.handlePlayerPlaceFurniture(player, location, id, event);
    }

    public void onInteractFurniture(Player player, Location location, String id, @Nullable Entity baseEntity, Cancellable event) {
        this.itemManager.handlePlayerInteractFurniture(player, location, id, baseEntity, event);
    }
}
