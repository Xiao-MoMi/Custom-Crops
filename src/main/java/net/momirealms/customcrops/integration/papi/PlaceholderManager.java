package net.momirealms.customcrops.integration.papi;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderManager extends Function {

    private SeasonPapi seasonPapi;
    private boolean hasPapi;

    public PlaceholderManager(CustomCrops plugin) {
        this.hasPapi = false;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.seasonPapi = new SeasonPapi(plugin);
            this.hasPapi = true;
        }
    }

    @Override
    public void load() {
        if (seasonPapi != null) seasonPapi.register();
    }

    @Override
    public void unload() {
        if (seasonPapi != null) seasonPapi.unregister();
    }

    public String parse(Player player, String text) {
        return hasPapi ? ParseUtil.setPlaceholders(player, text) : text;
    }
}
