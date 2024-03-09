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

package net.momirealms.customcrops.manager;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.PlaceholderManager;
import net.momirealms.customcrops.compatibility.papi.CCPapi;
import net.momirealms.customcrops.compatibility.papi.ParseUtils;
import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class PlaceholderManagerImpl extends PlaceholderManager {

    private final HashMap<String, String> customPlaceholderMap;
    private final CustomCropsPlugin plugin;
    private final boolean hasPapi;
    private CCPapi ccPapi;

    public PlaceholderManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.customPlaceholderMap = new HashMap<>();
        this.hasPapi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (hasPapi) {
            ccPapi = new CCPapi(plugin);
        }
    }

    @Override
    public void load() {
        if (ccPapi != null) {
            ccPapi.register();
        }
        this.loadCustomPlaceholders();
    }

    @Override
    public void unload() {
        if (ccPapi != null) {
            ccPapi.unregister();
        }
        this.customPlaceholderMap.clear();
    }

    public void loadCustomPlaceholders() {
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");
        ConfigurationSection section = config.getConfigurationSection("other-settings.placeholder-register");
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                this.customPlaceholderMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    @Override
    public String parse(@Nullable Player player, String text, Map<String, String> placeholders) {
        var list = detectPlaceholders(text);
        for (String papi : list) {
            String replacer = null;
            if (placeholders != null) {
                replacer = placeholders.get(papi);
            }
            if (replacer == null) {
                String custom = customPlaceholderMap.get(papi);
                if (custom != null) {
                    replacer = setPlaceholders(player, parse(player, custom, placeholders));
                }
            }
            if (replacer != null) {
                text = text.replace(papi, replacer);
            }
        }
        return text;
    }

    @Override
    public List<String> parse(@Nullable Player player, List<String> list, Map<String, String> replacements) {
        return list.stream()
                .map(s -> parse(player, s, replacements))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> detectPlaceholders(String text) {
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) placeholders.add(matcher.group());
        return placeholders;
    }

    @Override
    public String setPlaceholders(Player player, String text) {
        return hasPapi ? ParseUtils.setPlaceholders(player, text) : text;
    }
}
