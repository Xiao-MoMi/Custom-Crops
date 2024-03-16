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

import net.momirealms.customcrops.api.mechanic.misc.Reason;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import org.bukkit.Location;
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
    private final WorldPot pot;
    private final Entity entity;
    private final Reason reason;

    public PotBreakEvent(
            @Nullable Entity entity,
            @NotNull Location location,
            @NotNull WorldPot pot,
            @NotNull Reason reason
    ) {
        this.entity = entity;
        this.location = location;
        this.pot = pot;
        this.reason = reason;
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
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }


    /**
     * Get the pot's data
     * @return pot
     */
    @NotNull
    public WorldPot getPot() {
        return pot;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    @Nullable
    public Player getPlayer() {
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @NotNull
    public Reason getReason() {
        return reason;
    }
}