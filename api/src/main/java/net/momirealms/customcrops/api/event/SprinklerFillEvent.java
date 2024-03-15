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
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when a sprinkler is watered by the fill-methods set in each sprinkler's config
 */
public class SprinklerFillEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final Location location;
    private final PassiveFillMethod fillMethod;
    private final WorldSprinkler sprinkler;

    public SprinklerFillEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull PassiveFillMethod fillMethod,
            @NotNull WorldSprinkler sprinkler
    ) {
        super(player);
        this.itemInHand = itemInHand;
        this.location = location;
        this.fillMethod = fillMethod;
        this.sprinkler = sprinkler;
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
     * Get the item in player's hand
     * @return item in hand
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    /**
     * Get the sprinkler location
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public PassiveFillMethod getFillMethod() {
        return fillMethod;
    }

    @NotNull
    public WorldSprinkler getSprinkler() {
        return sprinkler;
    }
}
