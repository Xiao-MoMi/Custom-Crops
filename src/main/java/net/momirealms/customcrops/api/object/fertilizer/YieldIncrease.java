package net.momirealms.customcrops.api.object.fertilizer;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.api.object.Pair;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YieldIncrease extends FertilizerConfig {

    private final List<Pair<Double, Integer>> pairs;

    public YieldIncrease(String key, FertilizerType fertilizerType, int times, double chance, List<Pair<Double, Integer>> pairs, @Nullable String[] pot_whitelist, boolean beforePlant, @Nullable Particle particle, @Nullable Sound sound) {
        super(key, fertilizerType, times, chance, pot_whitelist, beforePlant, particle, sound);
        this.pairs = pairs;
    }

    public List<Pair<Double, Integer>> getPairs() {
        return pairs;
    }

    public int getAmountBonus() {
        for (Pair<Double, Integer> pair : pairs) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }
}
