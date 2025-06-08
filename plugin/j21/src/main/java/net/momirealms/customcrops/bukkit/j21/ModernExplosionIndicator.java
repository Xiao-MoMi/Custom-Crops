package net.momirealms.customcrops.bukkit.j21;

import net.momirealms.customcrops.api.core.world.ExplosionIndicator;
import org.bukkit.ExplosionResult;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ModernExplosionIndicator implements ExplosionIndicator {

    public ModernExplosionIndicator() {
    }

    @Override
    public boolean canDestroyBlocks(EntityExplodeEvent event) {
        return event.getExplosionResult() == ExplosionResult.DESTROY || event.getExplosionResult() == ExplosionResult.DESTROY_WITH_DECAY;
    }
}
