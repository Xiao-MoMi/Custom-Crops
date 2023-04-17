package net.momirealms.customcrops.api.object.sprinkler;

import net.momirealms.customcrops.api.object.HologramManager;
import org.jetbrains.annotations.NotNull;

public class SprinklerHologram {

    private final double offset;
    private final HologramManager.Mode mode;
    private final String content;
    private final int duration;
    private final String bar_left;
    private final String bar_full;
    private final String bar_empty;
    private final String bar_right;

    public SprinklerHologram(@NotNull String content, double offset, HologramManager.Mode mode, int duration, String bar_left, String bar_full, String bar_empty, String bar_right) {
        this.offset = offset;
        this.content = content;
        this.mode = mode;
        this.duration = duration;
        this.bar_left = bar_left;
        this.bar_full = bar_full;
        this.bar_empty = bar_empty;
        this.bar_right = bar_right;
    }

    public double getOffset() {
        return offset;
    }

    public HologramManager.Mode getMode() {
        return mode;
    }

    public int getDuration() {
        return duration;
    }

    public String getContent(int current, int storage) {
        return content.replace("{current}", String.valueOf(current))
                .replace("{storage}", String.valueOf(storage))
                .replace("{water_bar}", getWaterBar(current, storage));
    }

    public String getWaterBar(int current, int storage) {
        return bar_left +
                String.valueOf(bar_full).repeat(current) +
                String.valueOf(bar_empty).repeat(Math.max(storage - current, 0)) +
                bar_right;
    }

}
