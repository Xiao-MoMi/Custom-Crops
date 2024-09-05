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

package net.momirealms.customcrops.api.action.builtin;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.AbstractActionManager;
import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActionTimer<T> extends AbstractBuiltInAction<T> {
    final List<Action<T>> actions;
    final int delay, duration, period;
    final boolean async;
    public ActionTimer(
            BukkitCustomCropsPlugin plugin,
            AbstractActionManager<T> manager,
            Object args,
            double chance
    ) {
        super(plugin, chance);
        this.actions = new ArrayList<>();
        if (args instanceof Section section) {
            delay = section.getInt("delay", 2);
            duration = section.getInt("duration", 20);
            period = section.getInt("period", 2);
            async = section.getBoolean("async", false);
            Section actionSection = section.getSection("actions");
            if (actionSection != null)
                for (Map.Entry<String, Object> entry : actionSection.getStringRouteMappedValues(false).entrySet())
                    if (entry.getValue() instanceof Section innerSection)
                        actions.add(manager.parseAction(innerSection));
        } else {
            delay = 1;
            period = 1;
            async = false;
            duration = 20;
        }
    }
    @Override
    public void trigger(Context<T> context) {
        if (!checkChance()) return;
        Location location = context.arg(ContextKeys.LOCATION);
        SchedulerTask task;
        if (async) {
            task = plugin.getScheduler().asyncRepeating(() -> {
                for (Action<T> action : actions) {
                    action.trigger(context);
                }
            }, delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
        } else {
            task = plugin.getScheduler().sync().runRepeating(() -> {
                for (Action<T> action : actions) {
                    action.trigger(context);
                }
            }, delay, period, location);
        }
        plugin.getScheduler().asyncLater(task::cancel, duration * 50L, TimeUnit.MILLISECONDS);
    }

    public List<Action<T>> getActions() {
        return actions;
    }

    public int getDelay() {
        return delay;
    }

    public int getDuration() {
        return duration;
    }

    public int getPeriod() {
        return period;
    }

    public boolean isAsync() {
        return async;
    }
}
