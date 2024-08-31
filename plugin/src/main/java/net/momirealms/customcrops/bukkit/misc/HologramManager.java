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

package net.momirealms.customcrops.bukkit.misc;

import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import net.momirealms.customcrops.common.util.Pair;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.entity.FakeEntity;
import net.momirealms.sparrow.heart.feature.entity.armorstand.FakeArmorStand;
import net.momirealms.sparrow.heart.feature.entity.display.FakeTextDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HologramManager implements Listener, Reloadable {

    private final ConcurrentHashMap<UUID, HologramCache> hologramMap = new ConcurrentHashMap<>();
    private final BukkitCustomCropsPlugin plugin;
    private SchedulerTask cacheCheckTask;
    private static HologramManager manager;

    public static HologramManager getInstance() {
        return manager;
    }

    public HologramManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        manager = this;
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, plugin.getBoostrap());
        this.cacheCheckTask = plugin.getScheduler().asyncRepeating(() -> {
            ArrayList<UUID> removed = new ArrayList<>();
            long current = System.currentTimeMillis();
            for (Map.Entry<UUID, HologramCache> entry : hologramMap.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null || !player.isOnline()) {
                    removed.add(entry.getKey());
                } else {
                    entry.getValue().removeOutDated(current, player);
                }
            }
            for (UUID uuid : removed) {
                hologramMap.remove(uuid);
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
        for (Map.Entry<UUID, HologramCache> entry : hologramMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                entry.getValue().removeAll(player);
            }
        }
        if (cacheCheckTask != null) cacheCheckTask.cancel();
        this.hologramMap.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.hologramMap.remove(event.getPlayer().getUniqueId());
    }

    public void showHologram(Player player, Location location, Component component, int millis) {
        HologramCache hologramCache = hologramMap.get(player.getUniqueId());
        if (hologramCache != null) {
            hologramCache.showHologram(player, location, component, millis);
        } else {
            hologramCache = new HologramCache();
            hologramCache.showHologram(player, location, component, millis);
            hologramMap.put(player.getUniqueId(), hologramCache);
        }
    }

    public static class HologramCache {

        private final ConcurrentHashMap<Location, Pair<FakeEntity, Long>> cache = new ConcurrentHashMap<>();

        public void removeOutDated(long current, Player player) {
            ArrayList<Location> removed = new ArrayList<>();
            for (Map.Entry<Location, Pair<FakeEntity, Long>> entry : cache.entrySet()) {
                if (entry.getValue().right() < current) {
                    entry.getValue().left().destroy(player);
                    removed.add(entry.getKey());
                }
            }
            for (Location location : removed) {
                cache.remove(location);
            }
        }

        public void showHologram(Player player, Location location, Component component, int millis) {
            Pair<FakeEntity, Long> pair = cache.get(location);
            if (pair != null) {
                pair.left().destroy(player);
                pair.right(System.currentTimeMillis() + millis);
                if (VersionHelper.isVersionNewerThan1_19_4()) {
                    FakeTextDisplay fakeEntity = SparrowHeart.getInstance().createFakeTextDisplay(location.clone().add(0,1.25,0));
                    fakeEntity.name(AdventureHelper.componentToJson(component));
                    fakeEntity.rgba(0, 0, 0, 0);
                    fakeEntity.spawn(player);
                    pair.left(fakeEntity);
                } else {
                    FakeArmorStand fakeEntity = SparrowHeart.getInstance().createFakeArmorStand(location);
                    fakeEntity.name(AdventureHelper.componentToJson(component));
                    fakeEntity.small(true);
                    fakeEntity.invisible(true);
                    fakeEntity.spawn(player);
                    pair.left(fakeEntity);
                }
            } else {
                long removeTime = System.currentTimeMillis() + millis;
                if (VersionHelper.isVersionNewerThan1_19_4()) {
                    FakeTextDisplay fakeEntity = SparrowHeart.getInstance().createFakeTextDisplay(location.clone().add(0,1.25,0));
                    fakeEntity.name(AdventureHelper.componentToJson(component));
                    fakeEntity.rgba(0, 0, 0, 0);
                    fakeEntity.spawn(player);
                    this.cache.put(location, Pair.of(fakeEntity, removeTime));
                } else {
                    FakeArmorStand fakeEntity = SparrowHeart.getInstance().createFakeArmorStand(location);
                    fakeEntity.name(AdventureHelper.componentToJson(component));
                    fakeEntity.small(true);
                    fakeEntity.invisible(true);
                    fakeEntity.spawn(player);
                    this.cache.put(location, Pair.of(fakeEntity, removeTime));
                }
            }
        }

        public void removeAll(Player player) {
            for (Map.Entry<Location, Pair<FakeEntity, Long>> entry : this.cache.entrySet()) {
                entry.getValue().left().destroy(player);
            }
            cache.clear();
        }
    }
}
