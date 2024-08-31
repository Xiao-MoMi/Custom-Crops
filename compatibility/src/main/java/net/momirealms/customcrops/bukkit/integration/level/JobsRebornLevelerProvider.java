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

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.momirealms.customcrops.api.integration.LevelerProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JobsRebornLevelerProvider implements LevelerProvider {

    @Override
    public void addXp(@NotNull Player player, @NotNull String target, double amount) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        Job job = Jobs.getJob(target);
        if (jobsPlayer != null && jobsPlayer.isInJob(job))
            Jobs.getPlayerManager().addExperience(jobsPlayer, job, amount);
    }

    @Override
    public int getLevel(@NotNull Player player, @NotNull String target) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer != null) {
            List<JobProgression> jobs = jobsPlayer.getJobProgression();
            Job job = Jobs.getJob(target);
            for (JobProgression progression : jobs)
                if (progression.getJob().equals(job))
                    return progression.getLevel();
        }
        return 0;
    }

    @Override
    public String identifier() {
        return "JobsReborn";
    }
}
