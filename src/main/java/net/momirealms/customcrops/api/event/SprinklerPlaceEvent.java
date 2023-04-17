package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.object.sprinkler.SprinklerConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SprinklerPlaceEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack hand;
    private final Location location;
    private final SprinklerConfig sprinklerConfig;

    public SprinklerPlaceEvent(@NotNull Player who, ItemStack hand, Location location, SprinklerConfig sprinklerConfig) {
        super(who);
        this.hand = hand;
        this.location = location;
        this.sprinklerConfig = sprinklerConfig;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public ItemStack getHand() {
        return hand;
    }

    public Location getLocation() {
        return location;
    }

    public SprinklerConfig getSprinklerConfig() {
        return sprinklerConfig;
    }
}
