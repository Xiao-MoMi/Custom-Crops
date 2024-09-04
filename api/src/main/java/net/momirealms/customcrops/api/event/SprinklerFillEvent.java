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

import net.momirealms.customcrops.api.core.mechanic.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered when a sprinkler is filled with water using the fill methods defined in its configuration.
 */
public class SprinklerFillEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final SprinklerConfig config;
    private final ItemStack itemInHand;
    private final WateringMethod wateringMethod;
    private final CustomCropsBlockState blockState;
    private final EquipmentSlot hand;

    /**
     * Constructor for the SprinklerFillEvent.
     *
     * @param player         The player who is filling the sprinkler.
     * @param itemInHand     The ItemStack representing the item in the player's hand.
     * @param hand           The hand (main or offhand) used by the player for filling.
     * @param location       The location of the sprinkler being filled.
     * @param wateringMethod The method used to fill the sprinkler.
     * @param blockState     The state of the sprinkler block before it is filled.
     * @param config         The configuration of the sprinkler being filled.
     */
    public SprinklerFillEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull EquipmentSlot hand,
            @NotNull Location location,
            @NotNull WateringMethod wateringMethod,
            @NotNull CustomCropsBlockState blockState,
            @NotNull SprinklerConfig config
    ) {
        super(player);
        this.location = location;
        this.itemInHand = itemInHand;
        this.wateringMethod = wateringMethod;
        this.blockState = blockState;
        this.config = config;
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
     * Gets the location of the sprinkler being filled.
     *
     * @return the location of the sprinkler.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the ItemStack representing the item in the player's hand.
     *
     * @return the item in hand.
     */
    @NotNull
    public ItemStack itemInHand() {
        return itemInHand;
    }

    /**
     * Gets the configuration of the sprinkler being filled.
     *
     * @return the sprinkler configuration.
     */
    @NotNull
    public SprinklerConfig sprinklerConfig() {
        return config;
    }

    /**
     * Gets the method used to fill the sprinkler.
     *
     * @return the watering method.
     */
    @NotNull
    public WateringMethod wateringMethod() {
        return wateringMethod;
    }

    /**
     * Gets the state of the sprinkler block before it is filled.
     *
     * @return the block state of the sprinkler.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the hand (main or offhand) used by the player to fill the sprinkler.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }
}