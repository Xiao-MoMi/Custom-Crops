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

import net.momirealms.customcrops.api.core.mechanic.crop.BoneMeal;
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
 * The BoneMealUseEvent class represents an event triggered when a player uses bone meal
 * on a custom crop block in the CustomCrops plugin.
 */
public class BoneMealUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final BoneMeal boneMeal;
    private final CustomCropsBlockState blockState;
    private final ItemStack itemInHand;
    private final EquipmentSlot equipmentSlot;
    private final CropConfig config;

    /**
     * Constructor for the BoneMealUseEvent.
     *
     * @param player        The player who used the bone meal.
     * @param itemInHand    The ItemStack representing the item in the player's hand.
     * @param location      The location of the crop block affected by the bone meal.
     * @param boneMeal      The BoneMeal configuration being applied.
     * @param blockState    The state of the crop block before the bone meal is applied.
     * @param equipmentSlot The equipment slot where the bone meal item is held.
     * @param config        The crop configuration associated with the block being targeted.
     */
    public BoneMealUseEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull BoneMeal boneMeal,
            @NotNull CustomCropsBlockState blockState,
            @NotNull EquipmentSlot equipmentSlot,
            @NotNull CropConfig config
    ) {
        super(player);
        this.location = location;
        this.blockState = blockState;
        this.boneMeal = boneMeal;
        this.itemInHand = itemInHand;
        this.equipmentSlot = equipmentSlot;
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
     * Gets the location of the crop block affected by the bone meal.
     *
     * @return the location of the crop block.
     */
    @NotNull
    public Location location() {
        return location;
    }

    /**
     * Gets the crop configuration associated with the block being targeted.
     *
     * @return the crop configuration.
     */
    @NotNull
    public CropConfig cropConfig() {
        return config;
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
     * Gets the state of the crop block before the bone meal was applied.
     *
     * @return the block state of the crop.
     */
    @NotNull
    public CustomCropsBlockState blockState() {
        return blockState;
    }

    /**
     * Gets the equipment slot where the bone meal item is held.
     *
     * @return the equipment slot.
     */
    @NotNull
    public EquipmentSlot hand() {
        return equipmentSlot;
    }

    /**
     * Gets the configuration of the bone meal being used.
     *
     * @return the BoneMeal configuration.
     */
    @NotNull
    public BoneMeal boneMeal() {
        return boneMeal;
    }
}
