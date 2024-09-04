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
 * This event is called when a player interacts with a pot in the CustomCrops plugin.
 */
public class PotInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final PotConfig config;
    private final EquipmentSlot hand;

    /**
     * Constructor for the PotInteractEvent.
     *
     * @param who        The player who is interacting with the pot.
     * @param hand       The hand (main or offhand) used by the player for the interaction.
     * @param itemInHand The ItemStack representing the item in the player's hand.
     * @param config     The configuration of the pot being interacted with.
     * @param location   The location of the pot block being interacted with.
     * @param blockState The state of the pot block at the time of interaction.
     */
    public PotInteractEvent(
            @NotNull Player who,
            @NotNull EquipmentSlot hand,
            @NotNull ItemStack itemInHand,
            @NotNull PotConfig config,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState
    ) {
        super(who);
        this.itemInHand = itemInHand;
        this.location = location;
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
     * Gets the ItemStack representing the item in the player's hand.
     * If there is nothing in hand, it would return AIR.
     *
     * @return the item in hand.
     */
    @NotNull
    public ItemStack itemInHand() {
        return itemInHand;
    }

    /**
     * Gets the hand (main or offhand) used by the player to interact with the pot.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }

    /**
     * Gets the location of the pot block being interacted with.
     *
     * @return the location of the pot.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the state of the pot block at the time of interaction.
     *
     * @return the block state of the pot.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the configuration of the pot being interacted with.
     *
     * @return the pot configuration.
     */
    @NotNull
    public PotConfig potConfig() {
        return config;
    }
}
