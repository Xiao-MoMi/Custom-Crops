package net.momirealms.customcrops.api.core.world;

import org.bukkit.event.entity.EntityExplodeEvent;

public interface ExplosionIndicator {

    boolean canDestroyBlocks(EntityExplodeEvent event);

    class AlwaysTrue implements ExplosionIndicator {
        @Override
        public boolean canDestroyBlocks(EntityExplodeEvent event) {
            return true;
        }
    }
}
