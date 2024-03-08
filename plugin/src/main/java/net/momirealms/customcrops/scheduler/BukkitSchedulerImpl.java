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

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

public class BukkitSchedulerImpl implements SyncScheduler {

    private final CustomCropsPlugin plugin;

    public BukkitSchedulerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runSyncTask(Runnable runnable, Location location) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public CancellableTask runTaskSyncTimer(Runnable runnable, Location location, long delay, long period) {
        return new BukkitCancellableTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
    }

    @Override
    public CancellableTask runTaskSyncLater(Runnable runnable, Location location, long delay) {
        if (delay == 0) {
            if (Bukkit.isPrimaryThread()) runnable.run();
            else Bukkit.getScheduler().runTask(plugin, runnable);
            return new BukkitCancellableTask(null);
        }
        return new BukkitCancellableTask(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
    }

    public static class BukkitCancellableTask implements CancellableTask {

        private final BukkitTask bukkitTask;

        public BukkitCancellableTask(BukkitTask bukkitTask) {
            this.bukkitTask = bukkitTask;
        }

        @Override
        public void cancel() {
            if (this.bukkitTask != null)
                this.bukkitTask.cancel();
        }

        @Override
        public boolean isCancelled() {
            if (this.bukkitTask == null) return true;
            return this.bukkitTask.isCancelled();
        }
    }
}
