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

import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public interface PlaceholderManager extends Reloadable {

    Pattern PATTERN = Pattern.compile("\\{[^{}]+}");

    /**
     * Registers a custom placeholder.
     *
     * @param placeholder the placeholder to be registered
     * @param original    the original placeholder string for instance {@code %test_placeholder%}
     * @return true if the placeholder was successfully registered, false otherwise
     */
    boolean registerCustomPlaceholder(String placeholder, String original);

    /**
     * Registers a custom placeholder.
     *
     * @param placeholder the placeholder to be registered
     * @param provider    the value provider
     * @return true if the placeholder was successfully registered, false otherwise
     */
    boolean registerCustomPlaceholder(String placeholder, BiFunction<OfflinePlayer, Map<String, String>, String> provider);

    /**
     * Resolves all placeholders within a given text.
     *
     * @param text the text to resolve placeholders in.
     * @return a list of found placeholders.
     */
    List<String> resolvePlaceholders(String text);

    /**
     * Parses a single placeholder for the specified player, using the provided replacements.
     *
     * @param player        the player for whom the placeholder is being parsed
     * @param placeholder   the placeholder to be parsed
     * @param replacements  a map of replacements to be used
     * @return the parsed placeholder value
     */
    String parseSingle(@Nullable OfflinePlayer player, String placeholder, Map<String, String> replacements);

    /**
     * Parses placeholders in the given text for the specified player, using the provided replacements.
     *
     * @param player       the player for whom placeholders are being parsed
     * @param text         the text containing placeholders
     * @param replacements a map of replacements to be used
     * @return the text with placeholders replaced
     */
    String parse(@Nullable OfflinePlayer player, String text, Map<String, String> replacements);

    /**
     * Parses placeholders in the given list of texts for the specified player, using the provided replacements.
     *
     * @param player       the player for whom placeholders are being parsed
     * @param list         the list of texts containing placeholders
     * @param replacements a map of replacements to be used
     * @return the list of texts with placeholders replaced
     */
    List<String> parse(@Nullable OfflinePlayer player, List<String> list, Map<String, String> replacements);
}
