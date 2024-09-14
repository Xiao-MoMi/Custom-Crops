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

package net.momirealms.customcrops.api.misc.cooldown;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cooldowns for various actions or events.
 * Keeps track of cooldown times for different keys associated with player UUIDs.
 */
public class CoolDownManager implements Listener, Reloadable {

    private final ConcurrentHashMap<UUID, Data> dataMap;
    private final BukkitCustomCropsPlugin plugin;

    public CoolDownManager(BukkitCustomCropsPlugin plugin) {
        this.dataMap = new ConcurrentHashMap<>();
        this.plugin = plugin;
    }

    /**
     * Checks if a player is currently in cooldown for a specific key.
     *
     * @param uuid The UUID of the player.
     * @param key  The key associated with the cooldown.
     * @param time The cooldown time in milliseconds.
     * @return True if the player is in cooldown, false otherwise.
     */
    public boolean isCoolDown(UUID uuid, String key, long time) {
        Data data = this.dataMap.computeIfAbsent(uuid, k -> new Data());
        return data.isCoolDown(key, time);
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, plugin.getBootstrap());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void disable() {
        unload();
        this.dataMap.clear();
    }

    /**
     * Event handler for when a player quits the game. Removes their cooldown data.
     *
     * @param event The PlayerQuitEvent triggered when a player quits.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataMap.remove(event.getPlayer().getUniqueId());
    }

    public static class Data {

        private final Map<String, Long> coolDownMap;

        public Data() {
            this.coolDownMap = Collections.synchronizedMap(new HashMap<>());
        }

        /**
         * Checks if the player is in cooldown for a specific key.
         *
         * @param key   The key associated with the cooldown.
         * @param delay The cooldown delay in milliseconds.
         * @return True if the player is in cooldown, false otherwise.
         */
        public boolean isCoolDown(String key, long delay) {
            long time = System.currentTimeMillis();
            long last = coolDownMap.getOrDefault(key, time - delay);
            if (last + delay > time) {
                return true; // Player is in cooldown
            } else {
                coolDownMap.put(key, time);
                return false; // Player is not in cooldown
            }
        }
    }
}
