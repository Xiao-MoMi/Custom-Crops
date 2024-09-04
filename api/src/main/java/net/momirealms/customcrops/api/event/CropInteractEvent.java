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
 * An event that is triggered when a player interacts with a crop in the CustomCrops plugin.
 */
public class CropInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;
    private final CropConfig config;
    private final String stageItemID;

    /**
     * Constructor for the CropInteractEvent.
     *
     * @param who        The player who interacted with the crop.
     * @param itemInHand The ItemStack representing the item in the player's hand.
     * @param location   The location of the crop block being interacted with.
     * @param blockState The state of the crop block before interaction.
     * @param hand       The hand (main or offhand) used by the player.
     * @param config     The crop configuration associated with the crop block.
     * @param stageItemID The ID representing the stage of the crop.
     */
    public CropInteractEvent(
            @NotNull Player who,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull EquipmentSlot hand,
            @NotNull CropConfig config,
            @NotNull String stageItemID
    ) {
        super(who);
        this.location = location;
        this.blockState = blockState;
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.config = config;
        this.stageItemID = stageItemID;
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
     * Gets the location of the crop block being interacted with.
     *
     * @return the location of the crop block.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the ItemStack representing the item in the player's hand.
     * If there is nothing in hand, it would return AIR.
     *
     * @return the ItemStack in hand.
     */
    @NotNull
    public ItemStack itemInHand() {
        return itemInHand;
    }

    /**
     * Gets the crop configuration associated with the crop block.
     *
     * @return the crop configuration.
     */
    @NotNull
    public CropConfig cropConfig() {
        return config;
    }

    /**
     * Gets the state of the crop block before the interaction occurred.
     *
     * @return the block state of the crop.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the hand (main or offhand) used by the player to interact with the crop.
     *
     * @return the equipment slot representing the hand used.
     */
    @NotNull
    public EquipmentSlot hand() {
        return hand;
    }

    /**
     * Gets the ID representing the stage of the crop.
     *
     * @return the stage item ID.
     */
    @NotNull
    public String cropStageItemID() {
        return stageItemID;
    }
}
