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
import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An event that is triggered when a player attempts to use a watering can to add water to pots or sprinklers.
 */
public class WateringCanWaterPotEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;
    private final WateringCanConfig wateringCanConfig;
    private final PotConfig potConfig;
    private final List<Pair<Pos3, String>> potWithIDs;

    /**
     * Constructor for the WateringCanWaterPotEvent.
     *
     * @param player            The player who is using the watering can.
     * @param itemInHand        The ItemStack representing the watering can in the player's hand.
     * @param hand              The hand (main or offhand) used by the player to hold the watering can.
     * @param wateringCanConfig The configuration of the watering can being used.
     * @param potConfig         The configuration of the pot being watered.
     * @param potWithIDs        The list of pots with their positions and IDs.
     */
    public WateringCanWaterPotEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull EquipmentSlot hand,
            @NotNull WateringCanConfig wateringCanConfig,
            @NotNull PotConfig potConfig,
            List<Pair<Pos3, String>> potWithIDs
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.wateringCanConfig = wateringCanConfig;
        this.potConfig = potConfig;
        this.potWithIDs = potWithIDs;
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
     * Gets the configuration of the pot being watered.
     *
     * @return the pot configuration.
     */
    @NotNull
    public PotConfig potConfig() {
        return potConfig;
    }

    /**
     * Gets the list of pots with their positions and IDs.
     *
     * @return the list of pots with positions and IDs.
     */
    @NotNull
    public List<Pair<Pos3, String>> pots() {
        return potWithIDs;
    }
}
