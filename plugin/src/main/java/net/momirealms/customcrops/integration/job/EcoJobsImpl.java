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

package net.momirealms.customcrops.integration.job;

import com.willfp.ecojobs.api.EcoJobsAPI;
import com.willfp.ecojobs.jobs.Job;
import net.momirealms.customcrops.integration.JobInterface;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class EcoJobsImpl implements JobInterface {

    @Override
    public void addXp(Player player, double amount, @Nullable String jobName) {
        if (jobName == null) jobName = "farmer";
        for (Job job : EcoJobsAPI.getActiveJobs(player)) {
            if (job.getId().equals(jobName)) {
                EcoJobsAPI.giveJobExperience(player, job, amount);
                break;
            }
        }
    }

    @Override
    public int getLevel(Player player, @Nullable String jobName) {
//        Job job = Jobs.getByID("farmer");
//        if (job == null) return 0;
//        return EcoJobsAPI.getJobLevel(player, job);
        return 0;
    }
}
