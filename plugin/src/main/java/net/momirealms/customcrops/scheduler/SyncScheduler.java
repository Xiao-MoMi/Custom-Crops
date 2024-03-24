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

import net.momirealms.customcrops.api.scheduler.CancellableTask;
import org.bukkit.Location;
import org.bukkit.World;

public interface SyncScheduler {

    void runSyncTask(Runnable runnable, Location location);

    void runSyncTask(Runnable runnable, World world, int x, int z);

    CancellableTask runTaskSyncTimer(Runnable runnable, Location location, long delayTicks, long periodTicks);

    CancellableTask runTaskSyncLater(Runnable runnable, Location location, long delayTicks);
}
