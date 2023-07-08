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

package net.momirealms.customcrops.api.object.hologram;

import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.Tuple;
import net.momirealms.customcrops.util.FakeEntityUtils;
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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

public class HologramManager extends Function implements Listener {

    private final ConcurrentHashMap<UUID, HologramCache> hologramMap;
    private final CustomCrops plugin;
    private ScheduledFuture<?> scheduledFuture;

    public HologramManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.hologramMap = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        scheduledFuture = plugin.getScheduler().runTaskTimerAsync(() -> {
            ArrayList<UUID> removed = new ArrayList<>(4);
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
        }, 500, 500);
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
        if (scheduledFuture != null) scheduledFuture.cancel(false);
        this.hologramMap.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.hologramMap.remove(event.getPlayer().getUniqueId());
    }

    public void showHologram(Player player, Location location, Component component, int millis, Mode mode, TextDisplayMeta textDisplayMeta) {
        HologramCache hologramCache = hologramMap.get(player.getUniqueId());
        if (hologramCache != null) {
            hologramCache.showHologram(player, location, component, millis, mode, textDisplayMeta);
        } else {
            hologramCache = new HologramCache();
            hologramCache.showHologram(player, location, component, millis, mode, textDisplayMeta);
            hologramMap.put(player.getUniqueId(), hologramCache);
        }
    }

    public enum Mode {
        ARMOR_STAND,
        TEXT_DISPLAY
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
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getDestroyPacket(tuple.getMid()));
                }
            }
        }

        public void showHologram(Player player, Location location, Component component, int millis, Mode mode, TextDisplayMeta textDisplayMeta) {
            int entity_id = push(location, millis);
            if (entity_id == 0) {
                int random = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                tupleList.add(Tuple.of(location, random, System.currentTimeMillis() + millis));
                this.tuples = tupleList.toArray(new Tuple[0]);
                if (mode == Mode.ARMOR_STAND) {
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getSpawnPacket(random, location, EntityType.ARMOR_STAND));
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getVanishArmorStandMetaPacket(random, component));
                } else if (mode == Mode.TEXT_DISPLAY) {
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getSpawnPacket(random, location.clone().add(0,1,0), EntityType.TEXT_DISPLAY));
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getTextDisplayMetaPacket(random, component, textDisplayMeta));
                }
            } else {
                if (mode == Mode.ARMOR_STAND) {
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getVanishArmorStandMetaPacket(entity_id, component));
                } else if (mode == Mode.TEXT_DISPLAY) {
                    CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getTextDisplayMetaPacket(entity_id, component, textDisplayMeta));
                }
            }
        }

        public void removeAll(Player player) {
            for (Tuple<Location, Integer, Long> tuple : tuples) {
                CustomCrops.getProtocolManager().sendServerPacket(player, FakeEntityUtils.getDestroyPacket(tuple.getMid()));
            }
            this.tupleList.clear();
            this.tuples = null;
        }
    }
}
