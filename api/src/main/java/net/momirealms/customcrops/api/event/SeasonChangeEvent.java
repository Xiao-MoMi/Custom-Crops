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

import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An async event triggered when season changes
 */
public class SeasonChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Season season;
    private final World world;

    public SeasonChangeEvent(
            @NotNull World world,
            @NotNull Season season
    ) {
        super(true);
        this.world = world;
        this.season = season;
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
     * Get the new season
     * @return season
     */
    @NotNull
    public Season getSeason() {
        return season;
    }

    /**
     * Get the world
     * @return world
     */
    public World getWorld() {
        return world;
    }
}
