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
    private final String cropKey;
    private final Location location;
    private int point;
    private String cropItemID;

    public CropPlantEvent(
            @NotNull Player who,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull String cropKey,
            int point,
            @NotNull String cropItemID
    ) {
        super(who);
        this.itemInHand = itemInHand;
        this.location = location;
        this.cropKey = cropKey;
        this.point = point;
        this.cropItemID = cropItemID;
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
     * Get the crop config key
     * @return crop key
     */
    @NotNull
    public String getCropKey() {
        return cropKey;
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
     * Get the initial point
     * It would be 0 when planting
     * but might be a value higher than 0 when replanting
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

    /**
     * Get the crop stage model item id
     * @return crop model
     */
    @NotNull
    public String getCropModel() {
        return cropItemID;
    }

    /**
     * Set the crop model item id
     * @param cropItemID crop model item id
     */
    public void setCropModel(String cropItemID) {
        this.cropItemID = cropItemID;
    }
}
