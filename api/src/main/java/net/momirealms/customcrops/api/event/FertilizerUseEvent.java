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

import net.momirealms.customcrops.api.core.block.PotConfig;
import net.momirealms.customcrops.api.core.item.Fertilizer;
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
 * An event that triggered when player tries adding fertilizer to pot
 */
public class FertilizerUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final ItemStack itemInHand;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final Fertilizer fertilizer;
    private final EquipmentSlot hand;
    private final PotConfig config;

    public FertilizerUseEvent(
            @NotNull Player player,
            @NotNull ItemStack itemInHand,
            @NotNull Fertilizer fertilizer,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull EquipmentSlot hand,
            @NotNull PotConfig config
    ) {
        super(player);
        this.cancelled = false;
        this.itemInHand = itemInHand;
        this.fertilizer = fertilizer;
        this.location = location;
        this.blockState = blockState;
        this.hand = hand;
        this.config = config;
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
     * Get the fertilizer item in hand
     *
     * @return item in hand
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    /**
     * Get the pot's location
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public CustomCropsBlockState getBlockState() {
        return blockState;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    @NotNull
    public PotConfig getPotConfig() {
        return config;
    }

    /**
     * Get the fertilizer's config
     *
     * @return fertilizer config
     */
    @NotNull
    public Fertilizer getFertilizer() {
        return fertilizer;
    }
}
