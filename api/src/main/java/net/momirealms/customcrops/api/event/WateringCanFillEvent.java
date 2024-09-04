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

import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
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
 * An event that is triggered when a player attempts to add water to a watering can in the CustomCrops plugin.
 */
public class WateringCanFillEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final WateringCanConfig config;
    private final FillMethod fillMethod;
    private final Location location;
    private final EquipmentSlot hand;

    /**
     * Constructor for the WateringCanFillEvent.
     *
     * @param player     The player who is filling the watering can.
     * @param hand       The hand (main or offhand) used by the player to hold the watering can.
     * @param itemInHand The ItemStack representing the watering can in the player's hand.
     * @param location   The location where the filling action is taking place.
     * @param config     The configuration of the watering can being filled.
     * @param fillMethod The method used to fill the watering can.
     */
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
        cancelled = cancel;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the static handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
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
     * Gets the ItemStack representing the watering can in the player's hand.
     *
     * @return the watering can item.
     */
    @NotNull
    public ItemStack itemInHand() {
        return itemInHand;
    }

    /**
     * Gets the configuration of the watering can being filled.
     *
     * @return the watering can configuration.
     */
    @NotNull
    public WateringCanConfig wateringCanConfig() {
        return config;
    }

    /**
     * Gets the method used to fill the watering can.
     *
     * @return the fill method.
     */
    @NotNull
    public FillMethod fillMethod() {
        return fillMethod;
    }

    /**
     * Gets the hand (main or offhand) used by the player to hold the watering can.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }

    /**
     * Gets the location where the filling action is taking place.
     *
     * @return the location of the action.
     */
    @NotNull
    public Location location() {
        return location;
    }
}
