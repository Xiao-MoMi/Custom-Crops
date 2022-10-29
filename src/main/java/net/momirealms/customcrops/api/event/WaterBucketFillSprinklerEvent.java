package net.momirealms.customcrops.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WaterBucketFillSprinklerEvent extends PlayerEvent implements Cancellable {

    private final ItemStack itemStack;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public WaterBucketFillSprinklerEvent(@NotNull Player who, ItemStack itemStack) {
        super(who);
        this.itemStack = itemStack;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
