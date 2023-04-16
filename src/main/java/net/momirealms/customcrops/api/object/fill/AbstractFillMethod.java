package net.momirealms.customcrops.api.object.fill;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFillMethod {

    protected int amount;
    protected Particle particle;
    protected Sound sound;

    protected AbstractFillMethod(int amount, Particle particle, Sound sound) {
        this.amount = amount;
        this.particle = particle;
        this.sound = sound;
    }

    public int getAmount() {
        return amount;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }


    public Sound getSound() {
        return sound;
    }
}
