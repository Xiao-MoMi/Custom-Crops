package net.momirealms.customcrops.objects;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class WrappedSound {

    private final Sound.Source source;
    private final Key key;
    private final boolean enable;

    public WrappedSound(Sound.Source source, Key key, boolean enable) {
        this.source = source;
        this.key = key;
        this.enable = enable;
    }

    public Sound.Source getSource() {
        return source;
    }

    public Key getKey() {
        return key;
    }

    public boolean isEnable() {
        return enable;
    }
}
