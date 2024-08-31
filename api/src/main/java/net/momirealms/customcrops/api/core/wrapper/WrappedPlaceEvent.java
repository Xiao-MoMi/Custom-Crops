package net.momirealms.customcrops.api.core.wrapper;

import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class WrappedPlaceEvent {

    private final Player player;
    private final Location location;
    private final String placedID;
    private final EquipmentSlot hand;
    private final ItemStack item;
    private final String itemID;
    private final Cancellable event;
    private final CustomCropsWorld<?> world;

    public WrappedPlaceEvent(Player player, CustomCropsWorld<?> world, Location location, String placedID, EquipmentSlot hand, ItemStack item, String itemID, Cancellable event) {
        this.player = player;
        this.location = location;
        this.hand = hand;
        this.item = item;
        this.itemID = itemID;
        this.world = world;
        this.event = event;
        this.placedID = placedID;
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean cancel) {
        event.setCancelled(cancel);
    }

    public String placedID() {
        return placedID;
    }

    public CustomCropsWorld<?> world() {
        return world;
    }

    public Player player() {
        return player;
    }

    public Location location() {
        return location;
    }

    public EquipmentSlot hand() {
        return hand;
    }

    public ItemStack item() {
        return item;
    }

    public String itemID() {
        return itemID;
    }
}
