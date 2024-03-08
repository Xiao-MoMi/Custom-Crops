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

package net.momirealms.customcrops.manager;

import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.common.Tuple;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.utils.FakeEntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HologramManager implements Listener, Reloadable {

    private final ConcurrentHashMap<UUID, HologramCache> hologramMap;
    private final CustomCropsPlugin plugin;
    private CancellableTask cacheCheckTask;
    private static HologramManager manager;

    public static HologramManager getInstance() {
        return manager;
    }

    public HologramManager(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.hologramMap = new ConcurrentHashMap<>();
        manager = this;
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.cacheCheckTask = plugin.getScheduler().runTaskAsyncTimer(() -> {
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
        }, 200, 200, TimeUnit.MILLISECONDS);
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

    @SuppressWarnings("unchecked")
    public static class HologramCache {

        private final Vector<Tuple<Location, Integer, Long>> tupleList;
        private Tuple<Location, Integer, Long>[] tuples;

        public HologramCache() {
            this.tupleList = new Vector<>();
            this.tuples = new Tuple[0];
        }

        public int push(Location new_loc, int time) {
            for (Tuple<Location, Integer, Long> tuple : tuples) {
                if (new_loc.equals(tuple.getLeft())) {
                    tuple.setRight(System.currentTimeMillis() + time);
                    return tuple.getMid();
                }
            }
            return 0;
        }

        public void removeOutDated(long current, Player player) {
            for (Tuple<Location, Integer, Long> tuple : tuples) {
                if (tuple.getRight() < current) {
                    tupleList.remove(tuple);
                    this.tuples = tupleList.toArray(new Tuple[0]);
                    PacketManager.getInstance().send(player, FakeEntityUtils.getDestroyPacket(tuple.getMid()));
                }
            }
        }

        public void showHologram(Player player, Location location, Component component, int millis) {
            int entity_id = push(location, millis);
            if (entity_id == 0) {
                int random = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                tupleList.add(Tuple.of(location, random, System.currentTimeMillis() + millis));
                this.tuples = tupleList.toArray(new Tuple[0]);
                PacketManager.getInstance().send(player, FakeEntityUtils.getSpawnPacket(random, location, EntityType.ARMOR_STAND), FakeEntityUtils.getVanishArmorStandMetaPacket(random, component));
            } else {
                PacketManager.getInstance().send(player, FakeEntityUtils.getVanishArmorStandMetaPacket(entity_id, component));
            }
        }

        public void removeAll(Player player) {
            for (Tuple<Location, Integer, Long> tuple : tuples) {
                PacketManager.getInstance().send(player, FakeEntityUtils.getDestroyPacket(tuple.getMid()));
            }
            this.tupleList.clear();
            this.tuples = null;
        }
    }
}
