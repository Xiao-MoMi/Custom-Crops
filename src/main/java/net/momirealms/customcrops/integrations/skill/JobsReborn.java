package net.momirealms.customcrops.integrations.skill;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class JobsReborn implements SkillXP{

    @Override
    public void addXp(Player player, double amount) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

        if (jobsPlayer != null) {
            List<JobProgression> jobs = jobsPlayer.getJobProgression();

            Job job = Jobs.getJob("Farmer");

            for (JobProgression progression : jobs)
                if (progression.getJob().equals(job)){
                    progression.addExperience(amount);
                }
        }
    }
}
