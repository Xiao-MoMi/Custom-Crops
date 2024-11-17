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

package net.momirealms.customcrops.bukkit.integration.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomCropsPapi extends PlaceholderExpansion {

    private final BukkitCustomCropsPlugin plugin;

    public CustomCropsPapi(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        super.register();
    }

    public void unload() {
        super.unregister();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "customcrops";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "XiaoMoMi";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "3.6";
    }

    @Nullable
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String[] split = params.split("_", 2);
        switch (split[0]) {
            case "season" -> {
                if (split.length == 1) {
                    Player player = offlinePlayer.getPlayer();
                    if (player == null)
                        return null;
                    return plugin.getWorldManager().getSeason(player.getWorld()).translation();
                } else {
                    try {
                        return plugin.getWorldManager().getSeason(Bukkit.getWorld(split[1])).translation();
                    } catch (NullPointerException e) {
                        plugin.getPluginLogger().severe("World " + split[1] + " does not exist");
                    }
                }
            }
            case "date" -> {
                if (split.length == 1) {
                    Player player = offlinePlayer.getPlayer();
                    if (player == null)
                        return null;
                    return String.valueOf(plugin.getWorldManager().getDate(player.getWorld()));
                } else {
                    try {
                        return String.valueOf(plugin.getWorldManager().getDate(Bukkit.getWorld(split[1])));
                    } catch (NullPointerException e) {
                        plugin.getPluginLogger().severe("World " + split[1] + " does not exist");
                    }
                }
            }
        }
        return null;
    }
}
