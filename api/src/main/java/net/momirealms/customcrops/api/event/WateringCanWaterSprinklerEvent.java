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
import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered when a player attempts to use a watering can to add water to a sprinkler in the CustomCrops plugin.
 */
public class WateringCanWaterSprinklerEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;
    private final WateringCanConfig wateringCanConfig;
    private final SprinklerConfig sprinklerConfig;
    private final CustomCropsBlockState blockState;
    private final Location location;

    /**
     * Constructor for the WateringCanWaterSprinklerEvent.
     *
     * @param player            The player who is using the watering can.
     * @param itemInHand        The ItemStack representing the watering can in the player's hand.
     * @param hand              The hand (main or offhand) used by the player to hold the watering can.
     * @param wateringCanConfig The configuration of the watering can being used.
     * @param sprinklerConfig   The configuration of the sprinkler being watered.
     * @param blockState        The state of the block where the sprinkler is located.
     * @param location          The location of the sprinkler being watered.
     */
    public WateringCanWaterSprinklerEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull EquipmentSlot hand,
            @NotNull WateringCanConfig wateringCanConfig,
            @NotNull SprinklerConfig sprinklerConfig,
            @NotNull CustomCropsBlockState blockState,
            @NotNull Location location
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.wateringCanConfig = wateringCanConfig;
        this.sprinklerConfig = sprinklerConfig;
        this.blockState = blockState;
        this.location = location;
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
     * Gets the list of handlers for this event instance.
     *
     * @return the handler list.
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
     * Gets the hand (main or offhand) used by the player to hold the watering can.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }

    /**
     * Gets the configuration of the watering can being used.
     *
     * @return the watering can configuration.
     */
    @NotNull
    public WateringCanConfig wateringCanConfig() {
        return wateringCanConfig;
    }

    /**
     * Gets the configuration of the sprinkler being watered.
     *
     * @return the sprinkler configuration.
     */
    @NotNull
    public SprinklerConfig sprinklerConfig() {
        return sprinklerConfig;
    }

    /**
     * Gets the state of the block where the sprinkler is located.
     *
     * @return the block state of the sprinkler.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the location of the sprinkler being watered.
     *
     * @return the location of the sprinkler.
     */
    @NotNull
    public Location location() {
        return location;
    }
}
