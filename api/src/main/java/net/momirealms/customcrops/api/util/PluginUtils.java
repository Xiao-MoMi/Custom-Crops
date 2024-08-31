package net.momirealms.customcrops.api.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginUtils {

    public static boolean isEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    @SuppressWarnings("deprecation")
    public static String getPluginVersion(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            return plugin.getDescription().getVersion();
        }
        return "";
    }
}
