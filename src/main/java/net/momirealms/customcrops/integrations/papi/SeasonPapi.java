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

package net.momirealms.customcrops.integrations.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.api.utils.SeasonUtils;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.config.SeasonConfig;
import net.momirealms.customcrops.integrations.season.CCSeason;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeasonPapi extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "cseason";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (!SeasonConfig.enable) return MessageConfig.seasonDisabled;
        switch (params) {
            case "current" -> {
                if (!MainConfig.getWorldsList().contains(player.getWorld())) return MessageConfig.noSeason;
                return getSeasonText(player.getWorld());
            }
            case "days_left" -> {
                if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                if (!MainConfig.getWorldsList().contains(player.getWorld())) return MessageConfig.noSeason;
                return String.valueOf(SeasonConfig.duration - ((int) ((player.getWorld().getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration));
            }
            case "days_gone" -> {
                if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                if (!MainConfig.getWorldsList().contains(player.getWorld())) return MessageConfig.noSeason;
                return String.valueOf((int) ((player.getWorld().getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration + 1);
            }
            default -> {
                if (params.startsWith("current_")) {
                    World world = Bukkit.getWorld(params.substring(8));
                    if (world == null) return MessageConfig.noSeason;
                    if (!MainConfig.getWorldsList().contains(world)) return MessageConfig.noSeason;
                    return getSeasonText(world);
                }
                if (params.startsWith("days_left_")) {
                    if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                    World world = Bukkit.getWorld(params.substring(10));
                    if (world == null) return MessageConfig.noSeason;
                    if (!MainConfig.getWorldsList().contains(world)) return MessageConfig.noSeason;
                    return String.valueOf(SeasonConfig.duration - ((int) ((world.getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration));
                }
                if (params.startsWith("days_gone_")) {
                    if (!SeasonConfig.auto) return MessageConfig.autoSeasonDisabled;
                    World world = Bukkit.getWorld(params.substring(10));
                    if (world == null) return MessageConfig.noSeason;
                    if (!MainConfig.getWorldsList().contains(world)) return MessageConfig.noSeason;
                    return String.valueOf((int) ((world.getFullTime() / 24000L) % (SeasonConfig.duration * 4)) % SeasonConfig.duration + 1);
                }
            }
        }
        return "null";
    }

    private String getSeasonText(World world) {
        CCSeason season = SeasonUtils.getSeason(world);
        return switch (season) {
            case SPRING -> MessageConfig.spring;
            case SUMMER -> MessageConfig.summer;
            case AUTUMN -> MessageConfig.autumn;
            case WINTER -> MessageConfig.winter;
            default -> throw new IllegalStateException("Unexpected value: " + season);
        };
    }
}
