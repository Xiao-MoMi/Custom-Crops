package net.momirealms.customcrops.api.manager;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class PlaceholderManager {

    private static PlaceholderManager instance;

    public PlaceholderManager() {
        instance = this;
    }

    public static PlaceholderManager getInstance() {
        return instance;
    }

    public abstract String parse(Player player, String text, Map<String, String> vars);

    public abstract List<String> parse(Player player, List<String> text, Map<String, String> vars);
}
