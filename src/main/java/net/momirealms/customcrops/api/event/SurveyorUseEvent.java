package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SurveyorUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Fertilizer fertilizer;
    private final Location potLoc;

    public SurveyorUseEvent(@NotNull Player who, @Nullable Fertilizer fertilizer, Location potLoc) {
        super(who);
        this.cancelled = false;
        this.fertilizer = fertilizer;
        this.potLoc = potLoc;
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

    @Nullable
    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public Location getPotLoc() {
        return potLoc;
    }
}
