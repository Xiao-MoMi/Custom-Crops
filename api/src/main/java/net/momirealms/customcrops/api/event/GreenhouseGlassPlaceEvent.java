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

import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered when a player places a greenhouse glass block in the CustomCrops plugin.
 */
public class GreenhouseGlassPlaceEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final String glassItemID;

    /**
     * Constructor for the GreenhouseGlassPlaceEvent.
     *
     * @param who        The player who is placing the greenhouse glass block.
     * @param location   The location where the greenhouse glass block is being placed.
     * @param glassItemID The item ID representing the glass type being placed.
     * @param blockState The state of the block where the greenhouse glass is placed.
     */
    public GreenhouseGlassPlaceEvent(
            @NotNull Player who,
            @NotNull Location location,
            @NotNull String glassItemID,
            @NotNull CustomCropsBlockState blockState
    ) {
        super(who);
        this.location = location;
        this.glassItemID = glassItemID;
        this.blockState = blockState;
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
     * Gets the location where the greenhouse glass block is being placed.
     *
     * @return the location of the glass block.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the state of the block where the greenhouse glass is placed.
     *
     * @return the block state of the glass.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the item ID representing the glass type being placed.
     *
     * @return the glass item ID.
     */
    @NotNull
    public String glassItemID() {
        return glassItemID;
    }
}
