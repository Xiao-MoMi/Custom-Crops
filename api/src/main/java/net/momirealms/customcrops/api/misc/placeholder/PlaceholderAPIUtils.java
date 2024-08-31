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

package net.momirealms.customcrops.api.misc.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Utility class for interacting with the PlaceholderAPI.
 * Provides methods to parse placeholders in strings for both online and offline players.
 */
public class PlaceholderAPIUtils {

    /**
     * Parses placeholders in the provided text for an online player.
     *
     * @param player The online player for whom the placeholders should be parsed.
     * @param text   The text containing placeholders to be parsed.
     * @return The text with parsed placeholders.
     */
    public static String parse(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    /**
     * Parses placeholders in the provided text for an offline player.
     *
     * @param player The offline player for whom the placeholders should be parsed.
     * @param text   The text containing placeholders to be parsed.
     * @return The text with parsed placeholders.
     */
    public static String parse(OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
