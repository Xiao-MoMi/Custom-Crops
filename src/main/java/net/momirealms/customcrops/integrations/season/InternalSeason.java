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

package net.momirealms.customcrops.integrations.season;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SeasonConfig;
import net.momirealms.customcrops.objects.Function;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class InternalSeason extends Function implements SeasonInterface {

    private ConcurrentHashMap<World, CCSeason> seasonHashMap;
    private BukkitTask task;

    public InternalSeason() {
        load();
    }

    @Override
    public void load() {
        super.load();
        this.seasonHashMap = new ConcurrentHashMap<>();
        startTimer();
    }

    @Override
    public void unload() {
        super.unload();
        this.seasonHashMap.clear();
        if (task != null) task.cancel();
    }

    @Override
    public boolean isWrongSeason(World world, @Nullable CCSeason[] seasonList) {
        if (seasonList == null) return false;
        for (CCSeason season : seasonList) {
            if (season == seasonHashMap.get(world)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void unloadWorld(World world) {
        seasonHashMap.remove(world);
    }

    @Override
    @NotNull
    public CCSeason getSeason(World world) {
        CCSeason season;
        if (MainConfig.syncSeason) season = seasonHashMap.get(MainConfig.syncWorld);
        else season = seasonHashMap.get(world);
        if (season == null) {
            season = countSeason(world);
            setSeason(season, world);
        }
        return season;
    }

    @Override
    public void setSeason(CCSeason season, World world) {
        if (season == CCSeason.UNKNOWN) {
            setSeason(countSeason(world), world);
        }
        else {
            if (MainConfig.syncSeason && world != MainConfig.syncWorld) {
                seasonHashMap.put(world, getSeason(MainConfig.syncWorld));
                return;
            }
            CCSeason oldSeason = seasonHashMap.put(world, season);
            if (!MainConfig.enableSeasonBroadcast) return;
            if (oldSeason == null) return;
            // season changed
            if (oldSeason != season) {
                Collection<? extends Player> players;
                if (MainConfig.syncSeason) players = Bukkit.getOnlinePlayers();
                else players = world.getPlayers();

                switch (season) {
                    case SPRING -> players.forEach(player -> {
                        for (String msg : MainConfig.springMsg) {
                            AdventureUtil.playerMessage(player, msg);
                        }
                    });
                    case SUMMER -> players.forEach(player -> {
                        for (String msg : MainConfig.summerMsg) {
                            AdventureUtil.playerMessage(player, msg);
                        }
                    });
                    case AUTUMN -> players.forEach(player -> {
                        for (String msg : MainConfig.autumnMsg) {
                            AdventureUtil.playerMessage(player, msg);
                        }
                    });
                    case WINTER -> players.forEach(player -> {
                        for (String msg : MainConfig.winterMsg) {
                            AdventureUtil.playerMessage(player, msg);
                        }
                    });
                }
            }
        }
    }

    private void startTimer() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!SeasonConfig.auto) return;
                for (World world : MainConfig.getWorldsArray()) {
                    if (world.getTime() < 100) {
                        setSeason(countSeason(world), world);
                    }
                }
            }
        }.runTaskTimerAsynchronously(CustomCrops.plugin, 0, 100);
    }

    private CCSeason countSeason(World world) {
        int season = (int) ((world.getFullTime() / 24000L) % (SeasonConfig.duration * 4)) / SeasonConfig.duration;
        return switch (season) {
            case 0 -> CCSeason.SPRING;
            case 1 -> CCSeason.SUMMER;
            case 2 -> CCSeason.AUTUMN;
            case 3 -> CCSeason.WINTER;
            default -> CCSeason.UNKNOWN;
        };
    }
}
