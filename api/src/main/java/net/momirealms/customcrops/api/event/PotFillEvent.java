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

import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when a pot is watered by the fill-methods set in each pot's config
 */
public class PotFillEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final WorldPot pot;
    private final ItemStack itemInHand;
    private final PassiveFillMethod fillMethod;

    public PotFillEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull PassiveFillMethod fillMethod,
            @NotNull WorldPot pot
    ) {
        super(player);
        this.location = location;
        this.itemInHand = itemInHand;
        this.pot = pot;
        this.fillMethod = fillMethod;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
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
     * Get the pot location
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }


    /**
     * Get the pot's data
     *
     * @return pot
     */
    @NotNull
    public WorldPot getPot() {
        return pot;
    }

    /**
     * Get the item in hand
     *
     * @return item in hand
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    /**
     * Get the passive fill method
     *
     * @return passive fill method
     */
    @NotNull
    public PassiveFillMethod getFillMethod() {
        return fillMethod;
    }
}