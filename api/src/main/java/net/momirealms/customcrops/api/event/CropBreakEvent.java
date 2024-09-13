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

import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The CropBreakEvent class represents an event that occurs when a crop block is broken
 * in the CustomCrops plugin. This event is triggered under various circumstances such
 * as a player breaking the crop, trampling, explosions, or specific actions.
 */
public class CropBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity entityBreaker;
    private final Block blockBreaker;
    private final Location location;
    private final CropConfig config;
    private final String stageItemID;
    private final CustomCropsBlockState blockState;
    private final BreakReason reason;

    /**
     * Constructor for the CropBreakEvent.
     *
     * @param entityBreaker  The entity that caused the crop to break (can be null).
     * @param blockBreaker   The block that caused the crop to break (can be null).
     * @param config         The crop configuration associated with the crop.
     * @param stageItemID    The item ID representing the stage of the crop.
     * @param location       The location of the crop block that is being broken.
     * @param blockState     The data of the crop block before it was broken.
     * @param reason         The reason for the crop break.
     */
    public CropBreakEvent(
            @Nullable Entity entityBreaker,
            @Nullable Block blockBreaker,
            @NotNull CropConfig config,
            @NotNull String stageItemID,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull BreakReason reason
    ) {
        this.location = location;
        this.blockState = blockState;
        this.reason = reason;
        this.entityBreaker = entityBreaker;
        this.blockBreaker = blockBreaker;
        this.config = config;
        this.stageItemID = stageItemID;
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
     * Gets the location of the crop block that is being broken.
     *
     * @return the location of the broken crop block.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the block responsible for breaking the crop, if applicable.
     *
     * @return the block that caused the break, or null if not applicable.
     */
    @Nullable
    public Block blockBreaker() {
        return blockBreaker;
    }

    /**
     * Gets the entity responsible for breaking the crop, if applicable.
     *
     * @return the entity that caused the break, or null if not applicable.
     */
    @Nullable
    public Entity entityBreaker() {
        return entityBreaker;
    }

    /**
     * Gets the state of the crop block before it was broken.
     *
     * @return the block state before breakage, or null if not applicable.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the item ID representing the stage of the crop.
     *
     * @return the stage item ID.
     */
    @NotNull
    public String cropStageItemID() {
        return stageItemID;
    }

    /**
     * Gets the crop configuration associated with the broken block.
     *
     * @return the crop configuration.
     */
    @NotNull
    public CropConfig cropConfig() {
        return config;
    }

    /**
     * Gets the reason for the crop block breakage.
     *
     * @return the reason for the break.
     */
    @NotNull
    public BreakReason reason() {
        return reason;
    }
}
