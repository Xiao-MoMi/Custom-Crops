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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CoolDownManager implements Listener, Reloadable {

    private final ConcurrentHashMap<UUID, Data> dataMap;
    private final CustomCropsPlugin plugin;

    public CoolDownManager(CustomCropsPlugin plugin) {
        this.dataMap = new ConcurrentHashMap<>();
        this.plugin = plugin;
    }

    public boolean isCoolDown(UUID uuid, String key, long time) {
        Data data = this.dataMap.computeIfAbsent(uuid, k -> new Data());
        return data.isCoolDown(key, time);
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
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

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataMap.remove(event.getPlayer().getUniqueId());
    }

    public static class Data {

        private final HashMap<String, Long> coolDownMap;

        public Data() {
            this.coolDownMap = new HashMap<>();
        }

        public synchronized boolean isCoolDown(String key, long delay) {
            long time = System.currentTimeMillis();
            long last = coolDownMap.getOrDefault(key, time - delay);
            if (last + delay > time) {
                return true;
            } else {
                coolDownMap.put(key, time);
                return false;
            }
        }
    }
}
