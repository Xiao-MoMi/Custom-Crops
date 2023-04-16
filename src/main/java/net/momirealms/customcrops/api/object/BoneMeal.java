package net.momirealms.customcrops.api.object;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BoneMeal {

    private final String item;
    private final String returned;
    private final ArrayList<Pair<Double, Integer>> pairs;
    private final Sound sound;
    private final Particle particle;

    public BoneMeal(String item, @Nullable String returned, @NotNull ArrayList<Pair<Double, Integer>> pairs, @Nullable Sound sound, @Nullable Particle particle) {
        this.item = item;
        this.returned = returned;
        this.pairs = pairs;
        this.sound = sound;
        this.particle = particle;
    }

    public boolean isRightItem(String id) {
        return item.equals(id);
    }

    public ItemStack getReturned() {
        if (returned == null) return null;
        return CustomCrops.getInstance().getIntegrationManager().build(returned);
    }

    public int getPoint() {
        for (Pair<Double, Integer> pair : pairs) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }
}
