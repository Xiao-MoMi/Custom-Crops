package net.momirealms.customcrops.api.util;

import org.bukkit.event.Cancellable;

public class FakeCancellable implements Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
