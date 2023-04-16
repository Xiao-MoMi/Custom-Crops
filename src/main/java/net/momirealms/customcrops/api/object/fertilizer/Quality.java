package net.momirealms.customcrops.api.object.fertilizer;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public class Quality extends FertilizerConfig {

    private final double[] ratio;

    public Quality(String key, FertilizerType fertilizerType, int times, double chance, double[] ratio, @Nullable String[] pot_whitelist, boolean beforePlant, @Nullable Particle particle, @Nullable Sound sound) {
        super(key, fertilizerType, times, chance, pot_whitelist, beforePlant, particle, sound);
        this.ratio = ratio;
    }

    public double[] getRatio() {
        return ratio;
    }
}
