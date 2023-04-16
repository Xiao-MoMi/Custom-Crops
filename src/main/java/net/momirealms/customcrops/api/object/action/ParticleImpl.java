package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ParticleImpl implements Action {

    private final Particle particle;
    private final int amount;
    private final double offset;

    public ParticleImpl(Particle particle, int amount, double offset) {
        this.particle = particle;
        this.amount = amount;
        this.offset = offset;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return;
        Location location = crop_loc.getBukkitLocation();
        if (location == null) return;
        location.getWorld().spawnParticle(particle, location.clone().add(0.5,0.5,0.5), amount, offset, offset, offset);
    }
}
