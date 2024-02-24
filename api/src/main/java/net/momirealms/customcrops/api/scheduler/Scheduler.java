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

package net.momirealms.customcrops.api.scheduler;

import org.bukkit.Location;

import java.util.concurrent.TimeUnit;

public interface Scheduler {

    /**
     * Runs a task synchronously on the main server thread or region thread.
     *
     * @param runnable The task to run.
     * @param location The location associated with the task.
     */
    void runTaskSync(Runnable runnable, Location location);

    /**
     * Runs a task synchronously with a specified delay and period.
     *
     * @param runnable    The task to run.
     * @param location    The location associated with the task.
     * @param delayTicks  The delay in ticks before the first execution.
     * @param periodTicks The period between subsequent executions in ticks.
     * @return A CancellableTask for managing the scheduled task.
     */
    CancellableTask runTaskSyncTimer(Runnable runnable, Location location, long delayTicks, long periodTicks);

    /**
     * Runs a task asynchronously with a specified delay.
     *
     * @param runnable  The task to run.
     * @param delay     The delay before the task execution.
     * @param timeUnit  The time unit for the delay.
     * @return A CancellableTask for managing the scheduled task.
     */
    CancellableTask runTaskAsyncLater(Runnable runnable, long delay, TimeUnit timeUnit);

    /**
     * Runs a task asynchronously.
     *
     * @param runnable The task to run.
     */
    void runTaskAsync(Runnable runnable);

    /**
     * Runs a task synchronously with a specified delay.
     *
     * @param runnable  The task to run.
     * @param location  The location associated with the task.
     * @param delay     The delay before the task execution.
     * @param timeUnit  The time unit for the delay.
     * @return A CancellableTask for managing the scheduled task.
     */
    CancellableTask runTaskSyncLater(Runnable runnable, Location location, long delay, TimeUnit timeUnit);

    /**
     * Runs a task synchronously with a specified delay in ticks.
     *
     * @param runnable    The task to run.
     * @param location    The location associated with the task.
     * @param delayTicks  The delay in ticks before the task execution.
     * @return A CancellableTask for managing the scheduled task.
     */
    CancellableTask runTaskSyncLater(Runnable runnable, Location location, long delayTicks);

    /**
     * Runs a task asynchronously with a specified delay and period.
     *
     * @param runnable    The task to run.
     * @param delay       The delay before the first execution.
     * @param period      The period between subsequent executions.
     * @param timeUnit    The time unit for the delay and period.
     * @return A CancellableTask for managing the scheduled task.
     */
    CancellableTask runTaskAsyncTimer(Runnable runnable, long delay, long period, TimeUnit timeUnit);
}
