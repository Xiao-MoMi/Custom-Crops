/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that triggered when breaking a crop
 */
public class CropBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final String cropItemID;
    private final String cropKey;
    private final Location location;
    private final Entity entity;

    public CropBreakEvent(
            @Nullable Entity entity,
            @NotNull String cropItemID,
            @NotNull String cropKey,
            @NotNull Location location
    ) {
        this.entity = entity;
        this.cropItemID = cropItemID;
        this.location = location;
        this.cropKey = cropKey;
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

    /**
     * Get the crop item id in IA/Oraxen
     * @return item id
     */
    @NotNull
    public String getCropItemID() {
        return cropItemID;
    }

    /**
     * Get the crop location
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Would be null if the crop is not broken by an entity
     * @return entity
     */
    @Nullable
    public Entity getEntity() {
        return entity;
    }

    /**
     * Get the crop config key
     * @return crop key
     */
    @NotNull
    public String getCropKey() {
        return cropKey;
    }
}
