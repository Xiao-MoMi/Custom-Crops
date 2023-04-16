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

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.momirealms.customcrops.integration.JobInterface;
import org.bukkit.entity.Player;

import java.util.List;

public class JobsRebornImpl implements JobInterface {

    @Override
    public void addXp(Player player, double amount) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer != null) {
            List<JobProgression> jobs = jobsPlayer.getJobProgression();
            Job job = Jobs.getJob("Farmer");
            for (JobProgression progression : jobs) {
                if (progression.getJob().equals(job)) {
                    progression.addExperience(amount);
                    break;
                }
            }
        }
    }

    @Override
    public int getLevel(Player player) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer != null) {
            List<JobProgression> jobs = jobsPlayer.getJobProgression();
            Job job = Jobs.getJob("Farmer");
            for (JobProgression progression : jobs) {
                if (progression.getJob().equals(job)) {
                    return progression.getLevel();
                }
            }
        }
        return 0;
    }
}
