package net.momirealms.customcrops;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion{

    public Placeholders(CustomCrops customCrops) {
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

        if(params.equalsIgnoreCase("season")){
            return ConfigManager.Config.current
                    .replace("spring", ConfigManager.Config.spring)
                    .replace("summer", ConfigManager.Config.summer)
                    .replace("autumn", ConfigManager.Config.autumn)
                    .replace("winter", ConfigManager.Config.winter);
        }
        return null;
    }
}
