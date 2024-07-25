package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.mechanic.item.Crop;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An async event triggered when crop point changes
 */
public class CropGrowEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Crop crop;
    private final Location location;
    private final int previousPoint;
    private final int point;

    public CropGrowEvent(
            @NotNull Crop crop,
            @NotNull Location location,
            int previousPoint,
            int point
    ) {
        super(true);
        this.crop = crop;
        this.location = location;
        this.previousPoint = previousPoint;
        this.point = point;
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

    /**
     * Get the crop
     *
     * @return crop
     */
    @NotNull
    public Crop getCrop() {
        return crop;
    }

    /**
     * Get the location where the crop at
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the old point before update
     *
     * @return previousPoint
     */
    public int getPreviousPoint() {
        return previousPoint;
    }

    /**
     * Get the new point after update
     *
     * @return point
     */
    public int getPoint() {
        return point;
    }
}
