/*
 *  Copyright (C) <2022> <XiaoMoMi>
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
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The BoneMealDispenseEvent class represents an event that is triggered when a dispenser
 * applies bone meal to a custom crop block in the CustomCrops plugin. This event allows
 * for handling the action of bone meal being dispensed and applied to crops.
 */
public class BoneMealDispenseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final BoneMeal boneMeal;
    private final CustomCropsBlockState blockState;
    private final ItemStack boneMealItem;
    private final Block dispenser;

    /**
     * Constructor for the BoneMealDispenseEvent.
     *
     * @param dispenser    The dispenser block that dispensed the bone meal.
     * @param location     The location of the crop block affected by the bone meal.
     * @param blockState   The state of the crop block before the bone meal is applied.
     * @param boneMealItem The ItemStack representing the bone meal item used.
     * @param boneMeal     The BoneMeal configuration being used.
     */
    public BoneMealDispenseEvent(
            @NotNull Block dispenser,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull ItemStack boneMealItem,
            @NotNull BoneMeal boneMeal
    ) {
        this.location = location;
        this.blockState = blockState;
        this.boneMeal = boneMeal;
        this.boneMealItem = boneMealItem;
        this.dispenser = dispenser;
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
     * Gets the ItemStack representing the bone meal item used.
     *
     * @return the ItemStack of bone meal.
     */
    @NotNull
    public ItemStack boneMealItem() {
        return boneMealItem;
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
     * Gets the configuration of the bone meal being used.
     *
     * @return the BoneMeal configuration.
     */
    @NotNull
    public BoneMeal boneMeal() {
        return boneMeal;
    }

    /**
     * Gets the dispenser block that dispensed the bone meal.
     *
     * @return the dispenser block.
     */
    @NotNull
    public Block dispenser() {
        return dispenser;
    }
}
