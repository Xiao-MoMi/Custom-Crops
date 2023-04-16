package net.momirealms.customcrops.api.object.fill;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;

public class PositiveFillMethod extends AbstractFillMethod {

    private final InteractType type;
    private final String id;

    public PositiveFillMethod(InteractType type, String id, int amount, Particle particle, Sound sound) {
        super(amount, particle, sound);
        this.type = type;
        this.id = id;
    }

    public InteractType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public enum InteractType {
        BLOCK,
        ENTITY,
        MM
    }
}
