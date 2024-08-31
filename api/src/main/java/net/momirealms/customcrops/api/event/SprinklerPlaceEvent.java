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

import net.momirealms.customcrops.api.core.block.SprinklerConfig;
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
 * An event that triggered when placing a sprinkler
 */
public class SprinklerPlaceEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final Location location;
    private final SprinklerConfig config;
    private final CustomCropsBlockState blockState;
    private final EquipmentSlot hand;

    public SprinklerPlaceEvent(
            @NotNull Player who,
            @NotNull ItemStack itemInHand,
            @NotNull EquipmentSlot hand,
            @NotNull Location location,
            @NotNull SprinklerConfig config,
            CustomCropsBlockState blockState
    ) {
        super(who);
        this.itemInHand = itemInHand;
        this.location = location;
        this.config = config;
        this.hand = hand;
        this.blockState = blockState;
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
     *
     * @return item in hand
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    @NotNull
    public CustomCropsBlockState getBlockState() {
        return blockState;
    }

    /**
     * Get the sprinkler location
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public SprinklerConfig getSprinklerConfig() {
        return config;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }
}