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

import java.util.List;

/**
 * An event that is triggered when "quality-crops" action is executed
 */
public class QualityCropActionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Context<?> context;
    private final Location location;
    private final List<ItemStack> items;
    private final String[] qualityCrops;

    public QualityCropActionEvent(Context<?> context, Location location, String[] qualityCrops, List<ItemStack> itemStacks) {
        this.cancelled = false;
        this.context = context;
        this.location = location;
        this.items = itemStacks;
        this.qualityCrops = qualityCrops;
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
     * Gets the drops
     *
     * @return the drops
     */
    public List<ItemStack> items() {
        return items;
    }

    /**
     * Gets the quality crops' ids
     *
     * @return the quality crops' ids
     */
    public String[] qualityCrops() {
        return qualityCrops;
    }
}
