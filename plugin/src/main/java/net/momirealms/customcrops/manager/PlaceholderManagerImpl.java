package net.momirealms.customcrops.manager;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.PlaceholderManager;
import net.momirealms.customcrops.compatibility.papi.CCPapi;
import net.momirealms.customcrops.compatibility.papi.ParseUtils;
import org.bukkit.Bukkit;
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
    }

    @Override
    public void unload() {
        if (ccPapi != null) {
            ccPapi.unregister();
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
