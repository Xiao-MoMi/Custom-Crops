package net.momirealms.customcrops.mechanic.item.custom.oraxen;

import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurniturePlaceEvent;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import net.momirealms.customcrops.mechanic.item.custom.AbstractCustomListener;
import org.bukkit.event.EventHandler;

public class OraxenListener extends AbstractCustomListener {

    public OraxenListener(ItemManagerImpl itemManager) {
        super(itemManager);
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceFurniture(OraxenFurniturePlaceEvent event) {
        super.onPlaceFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        super.onBreakFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        super.onInteractFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getMechanic().getItemID(),
                event.getBaseEntity(),
                event
        );
    }
}
