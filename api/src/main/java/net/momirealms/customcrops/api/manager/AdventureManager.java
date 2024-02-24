package net.momirealms.customcrops.api.manager;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.common.Initable;
import org.bukkit.entity.Player;

public abstract class AdventureManager implements Initable {

    private static AdventureManager instance;

    public AdventureManager() {
        instance = this;
    }

    public static AdventureManager getInstance() {
        return instance;
    }

    public abstract void sendPlayerMessage(Player player, String text);

    public abstract void sendActionbar(Player player, String text);

    public abstract void sendSound(Player player, Sound.Source source, Key key, float pitch, float volume);

    public abstract void sendSound(Player player, Sound sound);

    public abstract Component getComponentFromMiniMessage(String text);

    public abstract String legacyToMiniMessage(String legacy);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isColorCode(char c);

    public abstract void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);
}
