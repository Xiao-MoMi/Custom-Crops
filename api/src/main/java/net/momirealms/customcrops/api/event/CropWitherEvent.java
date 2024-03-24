package net.momirealms.customcrops.api.event;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents an event triggered when a crop is about to wither,
 * occurring prior to its actual withering process.
 *
 * <p>
 * It provides functionality to handle the event related to a crop's withering,
 * allowing actions to be taken preemptively before the crop actually withers.
 * </p>
 *
 * @author GommeHD.net Development Team
 */
public class CropWitherEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull Location location;
    private boolean cancelled;

    public CropWitherEvent(@NotNull Location location) {
        this.location = location;
    }

    public @NotNull Location location() {
        return this.location;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
