package net.momirealms.customcrops.integration.papi;

import com.comphenix.protocol.PacketType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.customplugin.PlatformManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeasonPapi extends PlaceholderExpansion {

    private CustomCrops plugin;

    public SeasonPapi(CustomCrops plugin) {
        this.plugin = plugin;
    }

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
        return "3.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] split = params.split("_");
        switch (split.length) {
            case 1 -> {
                switch (split[0]) {
                    case "season" -> {
                        Player online_player = player.getPlayer();
                        if (online_player == null) return null;
                        return plugin.getIntegrationManager().getSeasonInterface().getSeason(player.getPlayer().getWorld().getName()).toString();
                    }
                    case "date" -> {
                        Player online_player = player.getPlayer();
                        if (online_player == null) return null;
                        return String.valueOf(plugin.getIntegrationManager().getSeasonInterface().getDate(player.getPlayer().getWorld().getName()));
                    }
                }
            }
            case 2 -> {
                switch (split[0]) {
                    case "season" -> {
                        return plugin.getIntegrationManager().getSeasonInterface().getSeason(split[1]).toString();
                    }
                    case "date" -> {
                        return String.valueOf(plugin.getIntegrationManager().getSeasonInterface().getDate(split[1]));
                    }
                }
            }
        }
        return null;
    }
}
