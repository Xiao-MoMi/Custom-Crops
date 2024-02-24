package net.momirealms.customcrops.mechanic.item.custom.itemsadder;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import net.momirealms.customcrops.mechanic.item.custom.AbstractCustomListener;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class ItemsAdderListener extends AbstractCustomListener {

    public ItemsAdderListener(ItemManagerImpl itemManager) {
        super(itemManager);
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceFurniture(FurniturePlaceSuccessEvent event) {
        Entity entity = event.getBukkitEntity();
        if (entity == null) return;
        super.onPlaceFurniture(
                event.getPlayer(),
                entity.getLocation(),
                event.getNamespacedID(),
                null
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakFurniture(FurnitureBreakEvent event) {
        CustomFurniture customFurniture = event.getFurniture();
        if (customFurniture == null) return;
        Entity entity = customFurniture.getEntity();
        if (entity == null) return;
        super.onBreakFurniture(
                event.getPlayer(),
                entity.getLocation(),
                event.getNamespacedID(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractFurniture(FurnitureInteractEvent event) {
        CustomFurniture customFurniture = event.getFurniture();
        if (customFurniture == null) return;
        Entity entity = customFurniture.getEntity();
        if (entity == null) return;
        super.onInteractFurniture(event.getPlayer(),
                entity.getLocation(),
                event.getNamespacedID(),
                entity,
                event
        );
    }
}
