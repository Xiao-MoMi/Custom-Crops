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

import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
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
 * An event that is triggered when a player plants a crop in the CustomCrops plugin.
 */
public class CropPlantEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final CropConfig config;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final EquipmentSlot hand;
    private int point;

    /**
     * Constructor for the CropPlantEvent.
     *
     * @param who        The player who is planting the crop.
     * @param itemInHand The ItemStack representing the item in the player's hand used for planting.
     * @param hand       The hand (main or offhand) used by the player for planting.
     * @param location   The location where the crop is being planted.
     * @param config     The crop configuration associated with the crop being planted.
     * @param blockState The state of the block where the crop is planted.
     * @param point      The initial point value associated with planting the crop.
     */
    public CropPlantEvent(
            @NotNull Player who,
            @NotNull ItemStack itemInHand,
            @NotNull EquipmentSlot hand,
            @NotNull Location location,
            @NotNull CropConfig config,
            @NotNull CustomCropsBlockState blockState,
            int point
    ) {
        super(who);
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.location = location;
        this.config = config;
        this.point = point;
        this.blockState = blockState;
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
     * Gets the ItemStack representing the seed item in the player's hand.
     *
     * @return the seed item.
     */
    @NotNull
    public ItemStack itemInHand() {
        return itemInHand;
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
     * Gets the state of the block where the crop is planted.
     *
     * @return the block state of the crop.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the hand (main or offhand) used by the player to plant the crop.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }

    /**
     * Gets the crop configuration associated with the crop being planted.
     *
     * @return the crop configuration.
     */
    @NotNull
    public CropConfig cropConfig() {
        return config;
    }

    /**
     * Gets the location where the crop is being planted.
     *
     * @return the location of the crop.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the initial point value associated with planting the crop.
     * This value is typically 0 when the crop is first planted.
     *
     * @return the initial point.
     */
    public int point() {
        return point;
    }

    /**
     * Sets the initial point value associated with planting the crop.
     *
     * @param point the new initial point value.
     */
    public void point(int point) {
        this.point = point;
    }
}
