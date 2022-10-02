package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.crop.Crop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class SeedPlantEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location seedLoc;
    private final Crop crop;

    public SeedPlantEvent(@NotNull Player who, Location seedLoc, Crop crop) {
        super(who);
        this.cancelled = false;
        this.seedLoc = seedLoc;
        this.crop = crop;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Location getSeedLoc() {
        return seedLoc;
    }

    public Crop getCrop() {
        return crop;
    }
}
