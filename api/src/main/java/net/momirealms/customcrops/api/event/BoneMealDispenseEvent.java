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

import net.momirealms.customcrops.api.mechanic.item.BoneMeal;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when the bone meal is dispensed
 */
public class BoneMealDispenseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final BoneMeal boneMeal;
    private final WorldCrop crop;
    private final ItemStack boneMealItem;
    private final Block dispenser;

    public BoneMealDispenseEvent(
            @NotNull Block dispenser,
            @NotNull ItemStack boneMealItem,
            @NotNull Location location,
            @NotNull BoneMeal boneMeal,
            @NotNull WorldCrop crop
    ) {
        this.location = location;
        this.crop = crop;
        this.boneMeal = boneMeal;
        this.boneMealItem = boneMealItem;
        this.dispenser = dispenser;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * Get the crop location
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the item in player's hand
     * If there's nothing in hand, it would return AIR
     * @return item in hand
     */
    @NotNull
    public ItemStack getBoneMealItem() {
        return boneMealItem;
    }

    /**
     * Get the crop's data
     *
     * @return crop data
     */
    @NotNull
    public WorldCrop getCrop() {
        return crop;
    }

    /**
     * Get the bone meal's config
     *
     * @return bone meal config
     */
    @NotNull
    public BoneMeal getBoneMeal() {
        return boneMeal;
    }

    /**
     * Get the dispenser block
     *
     * @return dispenser block
     */
    @NotNull
    public Block getDispenser() {
        return dispenser;
    }
}
