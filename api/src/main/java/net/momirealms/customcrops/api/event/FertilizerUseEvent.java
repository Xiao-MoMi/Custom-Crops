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

import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
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
 * An event that is triggered when a player tries to add fertilizer to a pot in the CustomCrops plugin.
 */
public class FertilizerUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final Fertilizer fertilizer;
    private final EquipmentSlot hand;
    private final PotConfig config;

    /**
     * Constructor for the FertilizerUseEvent.
     *
     * @param player     The player who is attempting to add fertilizer.
     * @param itemInHand The ItemStack representing the fertilizer item in the player's hand.
     * @param fertilizer The Fertilizer configuration being applied.
     * @param location   The location of the pot where the fertilizer is being added.
     * @param blockState The state of the block (pot) before the fertilizer is added.
     * @param hand       The hand (main or offhand) used by the player to apply the fertilizer.
     * @param config     The pot configuration associated with the pot being fertilized.
     */
    public FertilizerUseEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull Fertilizer fertilizer,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull EquipmentSlot hand,
            @NotNull PotConfig config
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.fertilizer = fertilizer;
        this.location = location;
        this.blockState = blockState;
        this.hand = hand;
        this.config = config;
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
     * Gets the ItemStack representing the fertilizer item in the player's hand.
     *
     * @return the item in hand.
     */
    @NotNull
    public ItemStack itemInHand() {
        return itemInHand;
    }

    /**
     * Gets the location of the pot where the fertilizer is being added.
     *
     * @return the location of the pot.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the state of the block (pot) before the fertilizer is added.
     *
     * @return the block state of the pot.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the hand (main or offhand) used by the player to apply the fertilizer.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }

    /**
     * Gets the pot configuration associated with the pot being fertilized.
     *
     * @return the pot configuration.
     */
    @NotNull
    public PotConfig potConfig() {
        return config;
    }

    /**
     * Gets the fertilizer being applied.
     *
     * @return the fertilizer configuration.
     */
    @NotNull
    public Fertilizer fertilizer() {
        return fertilizer;
    }
}
