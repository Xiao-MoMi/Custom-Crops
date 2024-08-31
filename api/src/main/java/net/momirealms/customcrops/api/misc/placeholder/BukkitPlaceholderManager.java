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

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.common.util.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * This class handles the registration and parsing of custom placeholders,
 * and integrates with PlaceholderAPI if available.
 */
public class BukkitPlaceholderManager implements PlaceholderManager {

    private final BukkitCustomCropsPlugin plugin;
    private boolean hasPapi;
    private final HashMap<String, BiFunction<OfflinePlayer, Map<String, String>, String>> customPlaceholderMap;
    private static BukkitPlaceholderManager instance;

    /**
     * Constructs a new BukkitPlaceholderManager instance.
     *
     * @param plugin the instance of {@link BukkitCustomCropsPlugin}
     */
    public BukkitPlaceholderManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.customPlaceholderMap = new HashMap<>();
        instance = this;
    }

    /**
     * Loads the placeholder manager, checking for PlaceholderAPI and registering default placeholders.
     */
    @Override
    public void load() {
        this.hasPapi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.customPlaceholderMap.put("{random}", (p, map) -> String.valueOf(RandomUtils.generateRandomDouble(0, 1)));
    }

    /**
     * Unloads the placeholder manager, clearing all registered placeholders.
     */
    @Override
    public void unload() {
        this.hasPapi = false;
        this.customPlaceholderMap.clear();
    }

    /**
     * Gets the singleton instance of BukkitPlaceholderManager.
     *
     * @return the singleton instance
     */
    public static BukkitPlaceholderManager getInstance() {
        return instance;
    }

    @Override
    public boolean registerCustomPlaceholder(String placeholder, String original) {
        if (this.customPlaceholderMap.containsKey(placeholder)) return false;
        this.customPlaceholderMap.put(placeholder, (p, map) -> PlaceholderAPIUtils.parse(p, parse(p, original, map)));
        return true;
    }

    @Override
    public boolean registerCustomPlaceholder(String placeholder, BiFunction<OfflinePlayer, Map<String, String>, String> provider) {
        if (this.customPlaceholderMap.containsKey(placeholder)) return false;
        this.customPlaceholderMap.put(placeholder, provider);
        return true;
    }

    @Override
    public List<String> resolvePlaceholders(String text) {
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) placeholders.add(matcher.group());
        return placeholders;
    }

    private String setPlaceholders(OfflinePlayer player, String text) {
        return hasPapi ? PlaceholderAPIUtils.parse(player, text) : text;
    }

    @Override
    public String parseSingle(@Nullable OfflinePlayer player, String placeholder, Map<String, String> replacements) {
        String result = null;
        if (replacements != null)
            result = replacements.get(placeholder);
        if (result != null)
            return result;
        String custom = Optional.ofNullable(customPlaceholderMap.get(placeholder)).map(supplier -> supplier.apply(player, replacements)).orElse(null);
        if (custom == null)
            return placeholder;
        return setPlaceholders(player, custom);
    }

    @Override
    public String parse(@Nullable OfflinePlayer player, String text, Map<String, String> replacements) {
        var list = resolvePlaceholders(text);
        for (String papi : list) {
            String replacer = null;
            if (replacements != null) {
                replacer = replacements.get(papi);
            }
            if (replacer == null) {
                String custom = Optional.ofNullable(customPlaceholderMap.get(papi)).map(supplier -> supplier.apply(player, replacements)).orElse(null);
                if (custom != null)
                    replacer = setPlaceholders(player, parse(player, custom, replacements));
            }
            if (replacer != null) {
                text = text.replace(papi, replacer);
            }
        }
        return text;
    }

    @Override
    public List<String> parse(@Nullable OfflinePlayer player, List<String> list, Map<String, String> replacements) {
        return list.stream()
                .map(s -> parse(player, s, replacements))
                .collect(Collectors.toList());
    }
}
