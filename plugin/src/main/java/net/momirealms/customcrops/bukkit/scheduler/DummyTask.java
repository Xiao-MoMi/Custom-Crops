package net.momirealms.customcrops.bukkit.scheduler;

import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;

public class DummyTask implements SchedulerTask {

    @Override
    public void cancel() {
    }

    @Override
    public boolean isCancelled() {
        return true;
    }
}
