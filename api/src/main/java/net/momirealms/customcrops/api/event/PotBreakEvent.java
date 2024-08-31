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

import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.block.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that triggered when breaking a pot
 */
public class PotBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final PotConfig config;
    private final CustomCropsBlockState blockState;
    private final Entity entityBreaker;
    private final Block blockBreaker;
    private final BreakReason reason;

    public PotBreakEvent(
            @Nullable Entity entityBreaker,
            @Nullable Block blockBreaker,
            @NotNull Location location,
            @NotNull PotConfig config,
            @NotNull CustomCropsBlockState blockState,
            @NotNull BreakReason reason
    ) {
        this.entityBreaker = entityBreaker;
        this.blockBreaker = blockBreaker;
        this.location = location;
        this.blockState = blockState;
        this.reason = reason;
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

    @Nullable
    public Entity getEntityBreaker() {
        return entityBreaker;
    }

    @Nullable
    public Player getPlayer() {
        if (entityBreaker instanceof Player player) {
            return player;
        }
        return null;
    }

    @NotNull
    public BreakReason getReason() {
        return reason;
    }

    @NotNull
    public PotConfig getPotConfig() {
        return config;
    }

    @NotNull
    public CustomCropsBlockState getBlockState() {
        return blockState;
    }

    @Nullable
    public Block getBlockBreaker() {
        return blockBreaker;
    }
}