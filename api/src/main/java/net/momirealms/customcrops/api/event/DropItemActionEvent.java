/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.context.Context;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered when "drop-item" action is executed
 */
public class DropItemActionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Context<?> context;
    private final Location location;
    private final String droppedItemID;
    private ItemStack item;

    public DropItemActionEvent(Context<?> context, Location location, String droppedItemID, ItemStack itemStack) {
        this.cancelled = false;
        this.context = context;
        this.location = location;
        this.item = itemStack;
        this.droppedItemID = droppedItemID;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the static handler list.
     */
    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the list of handlers for this event instance.
     *
     * @return the handler list.
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * Returns whether the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state of the event.
     *
     * @param cancel true to cancel the event, false otherwise.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the context related to this event
     *
     * @return context
     */
    public Context<?> context() {
        return context;
    }

    /**
     * Get the location of the dropped items
     *
     * @return location
     */
    public Location location() {
        return location;
    }

    /**
     * Gets the drop
     *
     * @return the drop
     */
    public ItemStack item() {
        return item;
    }

    /**
     * Sets the drop
     *
     * @param item the drop
     */
    public void item(ItemStack item) {
        this.item = item;
    }

    /**
     * Gets the dropped item's ID
     *
     * @return the dropped item's ID
     */
    public String droppedItemID() {
        return droppedItemID;
    }
}
