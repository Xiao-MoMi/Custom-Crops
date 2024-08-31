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

import net.momirealms.customcrops.api.core.item.WateringCanConfig;
import net.momirealms.customcrops.api.misc.water.FillMethod;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when player tries to add water to the watering-can
 */
public class WateringCanFillEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final WateringCanConfig config;
    private final FillMethod fillMethod;
    private final Location location;
    private final EquipmentSlot hand;

    public WateringCanFillEvent(
            @NotNull Player player,
            @NotNull EquipmentSlot hand,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull WateringCanConfig config,
            @NotNull FillMethod fillMethod
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.config = config;
        this.location = location;
        this.fillMethod = fillMethod;
        this.hand = hand;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the watering can item
     *
     * @return the watering can item
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    @NotNull
    public WateringCanConfig getConfig() {
        return config;
    }

    /**
     * Get the positive fill method
     *
     * @return positive fill method
     */
    @NotNull
    public FillMethod getFillMethod() {
        return fillMethod;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    /**
     * Get the location
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }
}
