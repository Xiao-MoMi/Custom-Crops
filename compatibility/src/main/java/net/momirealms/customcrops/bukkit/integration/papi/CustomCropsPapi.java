package net.momirealms.customcrops.bukkit.integration.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomCropsPapi extends PlaceholderExpansion {

    private final BukkitCustomCropsPlugin plugin;

    public CustomCropsPapi(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        super.register();
    }

    public void unload() {
        super.unregister();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "customcrops";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "XiaoMoMi";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "3.6";
    }

    @Nullable
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String[] split = params.split("_", 2);
        switch (split[0]) {
            case "season" -> {
                if (split.length == 1) {
                    Player player = offlinePlayer.getPlayer();
                    if (player == null)
                        return null;
                    return plugin.getWorldManager().getSeason(player.getWorld()).translation();
                } else {
                    try {
                        return plugin.getWorldManager().getSeason(Bukkit.getWorld(split[1])).translation();
                    } catch (NullPointerException e) {
                        plugin.getPluginLogger().severe("World " + split[1] + " does not exist");
                    }
                }
            }
            case "date" -> {
                if (split.length == 1) {
                    Player player = offlinePlayer.getPlayer();
                    if (player == null)
                        return null;
                    return String.valueOf(plugin.getWorldManager().getDate(player.getWorld()));
                } else {
                    try {
                        return String.valueOf(plugin.getWorldManager().getDate(Bukkit.getWorld(split[1])));
                    } catch (NullPointerException e) {
                        plugin.getPluginLogger().severe("World " + split[1] + " does not exist");
                    }
                }
            }
        }
        return null;
    }
}
