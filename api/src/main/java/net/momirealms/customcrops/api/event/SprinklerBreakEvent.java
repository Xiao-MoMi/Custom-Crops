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

import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.block.SprinklerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that triggered when breaking a sprinkler
 */
public class SprinklerBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final SprinklerConfig config;
    private final Entity entityBreaker;
    private final Block blockBreaker;
    private final BreakReason reason;

    public SprinklerBreakEvent(
            @Nullable Entity entityBreaker,
            @Nullable Block blockBreaker,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull SprinklerConfig config,
            @NotNull BreakReason reason
    ) {
        this.entityBreaker = entityBreaker;
        this.blockBreaker = blockBreaker;
        this.location = location;
        this.reason = reason;
        this.config = config;
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
     * Get the sprinkler location
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

    @NotNull
    public CustomCropsBlockState getBlockState() {
        return blockState;
    }

    @NotNull
    public SprinklerConfig getSprinklerConfig() {
        return config;
    }

    @Nullable
    public Block getBlockBreaker() {
        return blockBreaker;
    }

    @NotNull
    public BreakReason getReason() {
        return reason;
    }
}