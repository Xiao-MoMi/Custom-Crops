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
import net.momirealms.customcrops.api.misc.value.MathValue;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActionDelay<T> extends AbstractBuiltInAction<T> {

    private final List<Action<T>> actions;
    private final int delay;
    private final boolean async;

    public ActionDelay(
            BukkitCustomCropsPlugin plugin,
            AbstractActionManager<T> manager,
            Object args,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.actions = new ArrayList<>();
        if (args instanceof Section section) {
            delay = section.getInt("delay", 1);
            async = section.getBoolean("async", false);
            Section actionSection = section.getSection("actions");
            if (actionSection != null)
                for (Map.Entry<String, Object> entry : actionSection.getStringRouteMappedValues(false).entrySet())
                    if (entry.getValue() instanceof Section innerSection)
                        actions.add(manager.parseAction(innerSection));
        } else {
            delay = 1;
            async = false;
        }
    }

    @Override
    protected void triggerAction(Context<T> context) {
        Location location = context.arg(ContextKeys.LOCATION);
        if (async) {
            plugin.getScheduler().asyncLater(() -> {
                for (Action<T> action : actions)
                    action.trigger(context);
            }, delay * 50L, TimeUnit.MILLISECONDS);
        } else {
            plugin.getScheduler().sync().runLater(() -> {
                for (Action<T> action : actions)
                    action.trigger(context);
            }, delay, location);
        }
    }

    public List<Action<T>> actions() {
        return actions;
    }

    public int delay() {
        return delay;
    }

    public boolean async() {
        return async;
    }
}
