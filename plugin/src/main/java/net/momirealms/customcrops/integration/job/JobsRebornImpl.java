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
import com.gamingmesh.jobs.container.*;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.integration.JobInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JobsRebornImpl implements JobInterface, Listener {

    @Override
    public void addXp(Player player, double amount, String jobName) {
        if (jobName == null) jobName = "Farmer";
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer != null) {
            List<JobProgression> jobs = jobsPlayer.getJobProgression();
            Job job = Jobs.getJob(jobName);
            for (JobProgression progression : jobs) {
                if (progression.getJob().equals(job)) {
                    progression.addExperience(amount);
                    break;
                }
            }
        }
    }

    @Override
    public int getLevel(Player player, @Nullable String jobName) {
        if (jobName == null) jobName = "Farmer";
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer != null) {
            List<JobProgression> jobs = jobsPlayer.getJobProgression();
            Job job = Jobs.getJob(jobName);
            for (JobProgression progression : jobs) {
                if (progression.getJob().equals(job)) {
                    return progression.getLevel();
                }
            }
        }
        return 0;
    }

    @EventHandler
    public void onHarvest(CropBreakEvent event) {
        if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld())) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer == null) return;

        Jobs.action(jobsPlayer, new CustomCropsInfo(event.getCropItemID(), ActionType.MMKILL));
    }

    public static class CustomCropsInfo extends BaseActionInfo {
        private final String name;

        public CustomCropsInfo(String name, ActionType type) {
            super(type);
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getNameWithSub() {
            return this.name;
        }
    }
}
