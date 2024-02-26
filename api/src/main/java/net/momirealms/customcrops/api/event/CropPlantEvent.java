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

import net.momirealms.customcrops.api.mechanic.item.Crop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when planting a crop
 */
public class CropPlantEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final Crop crop;
    private final Location location;
    private int point;

    public CropPlantEvent(
            @NotNull Player who,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull Crop crop,
            int point
    ) {
        super(who);
        this.itemInHand = itemInHand;
        this.location = location;
        this.crop = crop;
        this.point = point;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Get the seed item
     * @return seed item
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
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
     * Get the crop's config
     *
     * @return crop
     */
    @NotNull
    public Crop getCrop() {
        return crop;
    }

    /**
     * Get the crop's location
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the initial point
     * It would be 0 when planting
     *
     * @return point
     */
    public int getPoint() {
        return point;
    }

    /**
     * Set the initial point
     * @param point point
     */
    public void setPoint(int point) {
        this.point = point;
    }
}
