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

import net.momirealms.customcrops.api.object.crop.CropConfig;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final CropConfig cropConfig;
    private final String cropItemID;
    private final Location location;
    private final Entity entity;

    /**
     * This event might be called when entity breaks the crop or player triggers the break action
     */
    public CropBreakEvent(@Nullable Entity entity, CropConfig cropConfig, String cropItemID, Location location) {
        this.entity = entity;
        this.cropConfig = cropConfig;
        this.cropItemID = cropItemID;
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
        return cropItemID;
    }

    public Location getLocation() {
        return location;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }
}
