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

package net.momirealms.customcrops.compatibility.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.MessageManager;
import net.momirealms.customcrops.api.util.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CCPapi extends PlaceholderExpansion {

    private final CustomCropsPlugin plugin;

    public CCPapi(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        super.register();
    }

    public void unload() {
        super.unregister();
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
        return "3.4";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String[] split = params.split("_", 2);

        Player player = offlinePlayer.getPlayer();
        if (player == null)
            return null;

        switch (split[0]) {
            case "season" -> {
                if (split.length == 1) {
                    return MessageManager.seasonTranslation(plugin.getIntegrationManager().getSeason(player.getWorld()));
                } else {
                    try {
                        return MessageManager.seasonTranslation(plugin.getIntegrationManager().getSeason(Bukkit.getWorld(split[1])));
                    } catch (NullPointerException e) {
                        LogUtils.severe("World " + split[1] + " does not exist");
                        e.printStackTrace();
                    }
                }
            }
            case "date" -> {
                if (split.length == 1) {
                    return String.valueOf(plugin.getIntegrationManager().getDate(player.getWorld()));
                } else {
                    try {
                        return String.valueOf(plugin.getIntegrationManager().getDate(Bukkit.getWorld(split[1])));
                    } catch (NullPointerException e) {
                        LogUtils.severe("World " + split[1] + " does not exist");
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}
