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

package net.momirealms.customcrops.integration.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeasonPapi extends PlaceholderExpansion {

    private final CustomCrops plugin;

    public SeasonPapi(CustomCrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "customcrops";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] split = params.split("_", 2);
        if (split.length == 1) {
            switch (split[0]) {
                case "season" -> {
                    Player online_player = player.getPlayer();
                    if (online_player == null) return null;
                    return plugin.getIntegrationManager().getSeasonInterface().getSeason(player.getPlayer().getWorld().getName()).getDisplay();
                }
                case "date" -> {
                    Player online_player = player.getPlayer();
                    if (online_player == null) return null;
                    return String.valueOf(plugin.getIntegrationManager().getSeasonInterface().getDate(player.getPlayer().getWorld().getName()));
                }
                case "daysleft" -> {
                    Player online_player = player.getPlayer();
                    if (online_player == null) return null;
                    return String.valueOf(1 + ConfigManager.seasonInterval - plugin.getIntegrationManager().getSeasonInterface().getDate(player.getPlayer().getWorld().getName()));
                }
            }
        } else if (split.length == 2) {
            switch (split[0]) {
                case "season" -> {
                    return plugin.getIntegrationManager().getSeasonInterface().getSeason(split[1]).getDisplay();
                }
                case "date" -> {
                    return String.valueOf(plugin.getIntegrationManager().getSeasonInterface().getDate(split[1]));
                }
                case "daysleft" -> {
                    return String.valueOf(1 + ConfigManager.seasonInterval - plugin.getIntegrationManager().getSeasonInterface().getDate(split[1]));
                }
            }
        }
        return null;
    }
}
