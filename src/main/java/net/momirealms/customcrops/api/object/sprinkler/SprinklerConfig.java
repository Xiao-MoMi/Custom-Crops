package net.momirealms.customcrops.api.object.sprinkler;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SprinklerConfig {

    private final String key;
    private final int storage;
    private final int range;
    private final Sound sound;
    private final ItemMode itemMode;
    private final String threeD;
    private final String twoD;
    private final PassiveFillMethod[] passiveFillMethods;

    public SprinklerConfig(String key, int storage, int range, @Nullable Sound sound, @NotNull ItemMode itemMode, @NotNull String threeD, @NotNull String twoD, @NotNull PassiveFillMethod[] passiveFillMethods) {
        this.key = key;
        this.storage = storage;
        this.range = range;
        this.sound = sound;
        this.itemMode = itemMode;
        this.threeD = threeD;
        this.twoD = twoD;
        this.passiveFillMethods = passiveFillMethods;
    }

    public String getKey() {
        return key;
    }

    public int getStorage() {
        return storage;
    }

    public int getRange() {
        return range;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    @NotNull
    public ItemMode getItemMode() {
        return itemMode;
    }

    @NotNull
    public String getThreeD() {
        return threeD;
    }

    @NotNull
    public String getTwoD() {
        return twoD;
    }

    @NotNull
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }
}
