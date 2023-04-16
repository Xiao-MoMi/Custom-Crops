package net.momirealms.customcrops.api.object.fertilizer;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.api.object.Pair;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpeedGrow extends FertilizerConfig {

    private final List<Pair<Double, Integer>> pairs;

    public SpeedGrow(String key, FertilizerType fertilizerType, int times, List<Pair<Double, Integer>> pairs, @Nullable String[] pot_whitelist, boolean beforePlant, @Nullable Particle particle, @Nullable Sound sound) {
        super(key, fertilizerType, times, 1, pot_whitelist, beforePlant, particle, sound);
        this.pairs = pairs;
    }

    public List<Pair<Double, Integer>> getPairs() {
        return pairs;
    }

    public int getPointBonus() {
        for (Pair<Double, Integer> pair : pairs) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }
}
