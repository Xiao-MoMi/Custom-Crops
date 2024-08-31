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

package net.momirealms.customcrops.api.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Utility class for handling events.
 */
public class EventUtils {

    /**
     * Fires an event and does not wait for any result.
     *
     * @param event the {@link Event} to be fired
     */
    public static void fireAndForget(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Fires an event and checks if it is cancelled.
     * This method only accepts events that implement {@link Cancellable}.
     *
     * @param event the {@link Event} to be fired
     * @return true if the event is cancelled, false otherwise
     * @throws IllegalArgumentException if the event is not cancellable
     */
    public static boolean fireAndCheckCancel(Event event) {
        if (!(event instanceof Cancellable cancellable))
            throw new IllegalArgumentException("Only cancellable events are allowed here");
        Bukkit.getPluginManager().callEvent(event);
        return cancellable.isCancelled();
    }
}
