/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Optional;

public class FoliaSchedulerImpl implements SyncScheduler {

    private final CustomCropsPlugin plugin;

    public FoliaSchedulerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runSyncTask(Runnable runnable, Location location) {
        Bukkit.getRegionScheduler().execute(plugin, Optional.ofNullable(location).orElse(LocationUtils.getAnyLocationInstance()), runnable);
    }

    @Override
    public CancellableTask runTaskSyncTimer(Runnable runnable, Location location, long delay, long period) {
        return new FoliaCancellableTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, (scheduledTask -> runnable.run()), delay, period));
    }

    @Override
    public CancellableTask runTaskSyncLater(Runnable runnable, Location location, long delay) {
        if (delay == 0) {
            return new FoliaCancellableTask(Bukkit.getRegionScheduler().run(plugin, location, (scheduledTask -> runnable.run())));
        }
        return new FoliaCancellableTask(Bukkit.getRegionScheduler().runDelayed(plugin, location, (scheduledTask -> runnable.run()), delay));
    }

    public static class FoliaCancellableTask implements CancellableTask {

        private final ScheduledTask scheduledTask;

        public FoliaCancellableTask(ScheduledTask scheduledTask) {
            this.scheduledTask = scheduledTask;
        }

        @Override
        public void cancel() {
            this.scheduledTask.cancel();
        }

        @Override
        public boolean isCancelled() {
            return this.scheduledTask.isCancelled();
        }
    }
}
