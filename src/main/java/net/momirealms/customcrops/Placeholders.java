package net.momirealms.customcrops;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Placeholders extends PlaceholderExpansion{


    private CustomCrops plugin;

    public Placeholders(CustomCrops customCrops) {
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
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        FileConfiguration config = CustomCrops.instance.getConfig();

        if(params.equalsIgnoreCase("season")){
            return Objects.requireNonNull(config.getString("current-season"))
                    .replace("spring", Objects.requireNonNull(config.getString("messages.spring")))
                    .replace("summer", Objects.requireNonNull(config.getString("messages.summer")))
                    .replace("autumn", Objects.requireNonNull(config.getString("messages.autumn")))
                    .replace("winter", Objects.requireNonNull(config.getString("messages.winter")));
        }
        return null;
    }
}
