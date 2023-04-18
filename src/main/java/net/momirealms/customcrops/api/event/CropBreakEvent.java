package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.object.crop.CropConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CropBreakEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final CropConfig cropConfig;
    private final String crop_id;
    private final Location location;

    public CropBreakEvent(@NotNull Player who, CropConfig cropConfig, String crop_id, Location location) {
        super(who);
        this.cropConfig = cropConfig;
        this.crop_id = crop_id;
        this.location = location;
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

    public CropConfig getCropConfig() {
        return cropConfig;
    }

    /**
     * Get the crop item id in IA/Oraxen
     * @return item id
     */
    public String getCropItemID() {
        return crop_id;
    }

    public Location getLocation() {
        return location;
    }
}
