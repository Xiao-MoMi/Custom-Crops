/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.bukkit.integration.level;

import com.willfp.ecojobs.api.EcoJobsAPI;
import com.willfp.ecojobs.jobs.Job;
import com.willfp.ecojobs.jobs.Jobs;
import net.momirealms.customcrops.api.integration.LevelerProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EcoJobsLevelerProvider implements LevelerProvider {

    @Override
    public void addXp(@NotNull Player player, @NotNull String target, double amount) {
        for (Job job : EcoJobsAPI.getActiveJobs(player)) {
            if (job.getId().equals(target)) {
                EcoJobsAPI.giveJobExperience(player, job, amount);
            }
        }
    }

    @Override
    public int getLevel(@NotNull Player player, @NotNull String target) {
        Job job = Jobs.getByID(target);
        if (job == null) return 0;
        return EcoJobsAPI.getJobLevel(player, job);
    }

    @Override
    public String identifier() {
        return "EcoJobs";
    }
}
