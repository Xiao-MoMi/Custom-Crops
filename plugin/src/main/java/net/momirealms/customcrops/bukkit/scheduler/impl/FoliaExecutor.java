/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customcrops.bukkit.scheduler.impl;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.momirealms.customcrops.common.plugin.scheduler.RegionExecutor;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class FoliaExecutor implements RegionExecutor<Location, World> {

    private final Plugin plugin;

    public FoliaExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run(Runnable r, Location l) {
        Optional.ofNullable(l).ifPresentOrElse(loc -> Bukkit.getRegionScheduler().execute(plugin, loc, r), () -> Bukkit.getGlobalRegionScheduler().execute(plugin, r));
    }

    @Override
    public void run(Runnable r, World world, int x, int z) {
        Bukkit.getRegionScheduler().execute(plugin, world, x, z, r);
    }

    @Override
    public SchedulerTask runLater(Runnable r, long delayTicks, Location l) {
        if (l == null) {
            if (delayTicks == 0) {
                return new FoliaCancellable(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> r.run(), delayTicks));
            } else {
                return new FoliaCancellable(Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> r.run()));
            }
        } else {
            if (delayTicks == 0) {
                return new FoliaCancellable(Bukkit.getRegionScheduler().run(plugin, l, scheduledTask -> r.run()));
            } else {
                return new FoliaCancellable(Bukkit.getRegionScheduler().runDelayed(plugin, l, scheduledTask -> r.run(), delayTicks));
            }
        }
    }

    @Override
    public SchedulerTask runRepeating(Runnable r, long delayTicks, long period, Location l) {
        if (l == null) {
            return new FoliaCancellable(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> r.run(), delayTicks, period));
        } else {
            return new FoliaCancellable(Bukkit.getRegionScheduler().runAtFixedRate(plugin, l, scheduledTask -> r.run(), delayTicks, period));
        }
    }

    public static class FoliaCancellable implements SchedulerTask {

        private final ScheduledTask task;

        public FoliaCancellable(ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            this.task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return this.task.isCancelled();
        }
    }
}
