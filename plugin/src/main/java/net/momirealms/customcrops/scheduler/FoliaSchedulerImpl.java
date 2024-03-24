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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class FoliaSchedulerImpl implements SyncScheduler {

    private final CustomCropsPlugin plugin;

    public FoliaSchedulerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runSyncTask(Runnable runnable, Location location) {
        if (location == null) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
        } else {
            Bukkit.getRegionScheduler().execute(plugin, location, runnable);
        }
    }

    @Override
    public void runSyncTask(Runnable runnable, World world, int x, int z) {
        Bukkit.getRegionScheduler().execute(plugin, world, x, z, runnable);
    }

    @Override
    public CancellableTask runTaskSyncTimer(Runnable runnable, Location location, long delay, long period) {
        if (location == null) {
            return new FoliaCancellableTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask -> runnable.run()), delay, period));
        }
        return new FoliaCancellableTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, (scheduledTask -> runnable.run()), delay, period));
    }

    @Override
    public CancellableTask runTaskSyncLater(Runnable runnable, Location location, long delay) {
        if (delay == 0) {
            if (location == null) {
                return new FoliaCancellableTask(Bukkit.getGlobalRegionScheduler().run(plugin, (scheduledTask -> runnable.run())));
            }
            return new FoliaCancellableTask(Bukkit.getRegionScheduler().run(plugin, location, (scheduledTask -> runnable.run())));
        }
        if (location == null) {
            return new FoliaCancellableTask(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask -> runnable.run()), delay));
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
