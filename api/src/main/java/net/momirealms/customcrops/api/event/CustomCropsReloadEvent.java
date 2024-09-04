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

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered when the CustomCrops plugin is reloaded.
 */
public class CustomCropsReloadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final BukkitCustomCropsPlugin plugin;

    /**
     * Constructor for the CustomCropsReloadEvent.
     *
     * @param plugin The instance of the CustomCrops plugin being reloaded.
     */
    public CustomCropsReloadEvent(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the static handler list.
     */
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * Gets the list of handlers for this event instance.
     *
     * @return the handler list.
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * Gets the instance of the CustomCrops plugin that is being reloaded.
     *
     * @return the plugin instance.
     */
    public BukkitCustomCropsPlugin plugin() {
        return plugin;
    }
}
