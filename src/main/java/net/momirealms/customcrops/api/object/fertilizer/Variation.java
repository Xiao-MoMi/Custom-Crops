package net.momirealms.customcrops.api.object.fertilizer;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public class Variation extends FertilizerConfig {

    public Variation(String key, FertilizerType fertilizerType, int times, double chance, @Nullable String[] pot_whitelist, boolean beforePlant, @Nullable Particle particle, @Nullable Sound sound) {
        super(key, fertilizerType, times, chance, pot_whitelist, beforePlant, particle, sound);
    }
}
