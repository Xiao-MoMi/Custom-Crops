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

import net.momirealms.customcrops.api.core.block.SprinklerConfig;
import net.momirealms.customcrops.api.core.item.WateringCanConfig;
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
 * An event that triggered when player tries to use watering-can to add water to pots/sprinklers
 */
public class WateringCanWaterSprinklerEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;
    private final WateringCanConfig wateringCanConfig;
    private final SprinklerConfig sprinklerConfig;
    private final CustomCropsBlockState blockState;
    private final Location location;

    public WateringCanWaterSprinklerEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull EquipmentSlot hand,
            @NotNull WateringCanConfig wateringCanConfig,
            @NotNull SprinklerConfig sprinklerConfig,
            @NotNull CustomCropsBlockState blockState,
            @NotNull Location location
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.wateringCanConfig = wateringCanConfig;
        this.sprinklerConfig = sprinklerConfig;
        this.blockState = blockState;
        this.location = location;
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

    /**
     * Get the watering can item
     *
     * @return watering can item
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    @NotNull
    public WateringCanConfig getWateringCanConfig() {
        return wateringCanConfig;
    }

    @NotNull
    public SprinklerConfig getSprinklerConfig() {
        return sprinklerConfig;
    }

    @NotNull
    public CustomCropsBlockState getBlockState() {
        return blockState;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }
}
