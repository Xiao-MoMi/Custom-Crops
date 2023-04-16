package net.momirealms.customcrops.api.object.wateringcan;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.api.object.fill.PositiveFillMethod;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WateringCanConfig {

    private final int width;
    private final int length;
    private final int storage;
    private final String[] pot_whitelist;
    private final String[] sprinkler_whitelist;
    private final boolean hasDynamicLore;
    private final boolean hasActionBar;
    private final Sound sound;
    private final Particle particle;
    private final List<String> loreTemplate;
    private final String actionBarMsg;
    private final String bar_left;
    private final String bar_full;
    private final String bar_empty;
    private final String bar_right;
    private final PositiveFillMethod[] positiveFillMethods;

    public WateringCanConfig(int width, int length, int storage,
                             boolean hasDynamicLore, boolean hasActionBar, @Nullable List<String> loreTemplate, @Nullable String actionBarMsg,
                             @Nullable String bar_left, @Nullable String bar_full, @Nullable String bar_empty, @Nullable String bar_right,
                             String[] pot_whitelist, String[] sprinkler_whitelist, @Nullable Sound sound, @Nullable Particle particle, @NotNull PositiveFillMethod[] positiveFillMethods) {
        this.width = width;
        this.length = length;
        this.storage = storage;
        this.hasDynamicLore = hasDynamicLore;
        this.hasActionBar = hasActionBar;
        this.loreTemplate = loreTemplate;
        this.actionBarMsg = actionBarMsg;
        this.bar_left = bar_left;
        this.bar_full = bar_full;
        this.bar_empty = bar_empty;
        this.bar_right = bar_right;
        this.pot_whitelist = pot_whitelist;
        this.sprinkler_whitelist = sprinkler_whitelist;
        this.sound = sound;
        this.particle = particle;
        this.positiveFillMethods = positiveFillMethods;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getStorage() {
        return storage;
    }

    public String getWaterBar(int current) {
        return bar_left +
                String.valueOf(bar_full).repeat(current) +
                String.valueOf(bar_empty).repeat(storage - current) +
                bar_right;
    }

    public boolean hasDynamicLore() {
        return hasDynamicLore;
    }

    public boolean hasActionBar() {
        return hasActionBar;
    }

    public String getActionBarMsg(int current) {
        assert actionBarMsg != null;
        return actionBarMsg
                .replace("{current}", String.valueOf(current))
                .replace("{storage}", String.valueOf(storage))
                .replace("{water_bar}", getWaterBar(current));
    }

    public List<String> getLore(int current) {
        assert loreTemplate != null;
        return loreTemplate.stream().map(line ->
                GsonComponentSerializer.gson().serialize(
                MiniMessage.miniMessage().deserialize(line
                        .replace("{current}", String.valueOf(current))
                        .replace("{storage}", String.valueOf(storage))
                        .replace("{water_bar}", getWaterBar(current))))).toList();
    }

    public String[] getPotWhitelist() {
        return pot_whitelist;
    }

    public String[] getSprinklerWhitelist() {
        return sprinkler_whitelist;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }

    @NotNull
    public PositiveFillMethod[] getPositiveFillMethods() {
        return positiveFillMethods;
    }
}
