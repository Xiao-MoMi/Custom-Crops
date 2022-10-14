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

package net.momirealms.customcrops.managers.timer;

import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.managers.CropManager;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {

    private final CropManager cropManager;

    public TimerTask(CropManager cropManager) {
        this.cropManager = cropManager;
    }

    @Override
    public void run() {
        if (!MainConfig.autoGrow) return;
        for (World world : MainConfig.getWorldsList()) {
            long time = world.getTime();
            if (time > 950 && time < 1051) {
                cropManager.grow(world, MainConfig.timeToGrow, MainConfig.timeToWork, MainConfig.timeToDry, false, false);
            }
        }
    }
}
