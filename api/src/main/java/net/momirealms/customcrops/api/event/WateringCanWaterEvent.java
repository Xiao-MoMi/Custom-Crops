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

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.item.WateringCan;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when player tries to use watering-can to add water to pots/sprinklers
 */
public class WateringCanWaterEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final WateringCan wateringCan;
    private final KeyItem potOrSprinkler;
    private final Location location;

    public WateringCanWaterEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull WateringCan wateringCan,
            @NotNull Location location,
            @NotNull KeyItem potOrSprinkler
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.wateringCan = wateringCan;
        this.location = location;
        this.potOrSprinkler = potOrSprinkler;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    @NotNull
    public WateringCan getWateringCan() {
        return wateringCan;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public KeyItem getPotOrSprinkler() {
        return potOrSprinkler;
    }
}
