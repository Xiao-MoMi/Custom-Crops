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

package net.momirealms.customcrops.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.SeasonManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Placeholders extends PlaceholderExpansion{

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
        return "1.2";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("season")){
            return Optional.ofNullable(SeasonManager.SEASON.get(player.getPlayer().getWorld().getName())).orElse(ConfigReader.Message.noSeason)
                    .replace("spring", ConfigReader.Message.spring)
                    .replace("summer", ConfigReader.Message.summer)
                    .replace("autumn", ConfigReader.Message.autumn)
                    .replace("winter", ConfigReader.Message.winter);
        }
        if (params.startsWith("season_")){
            return SeasonManager.SEASON.get(params.substring(7))
                    .replace("spring", ConfigReader.Message.spring)
                    .replace("summer", ConfigReader.Message.summer)
                    .replace("autumn", ConfigReader.Message.autumn)
                    .replace("winter", ConfigReader.Message.winter);
        }
        if (params.equalsIgnoreCase("nextseason")){
            return String.valueOf(ConfigReader.Season.duration - ((int) ((player.getPlayer().getWorld().getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) % ConfigReader.Season.duration));
        }
        if (params.startsWith("nextseason_")){
            return String.valueOf(ConfigReader.Season.duration - ((int) ((Bukkit.getWorld(params.substring(11)).getFullTime() / 24000L) % (ConfigReader.Season.duration * 4)) % ConfigReader.Season.duration));
        }
        return null;
    }
}
