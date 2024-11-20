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
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionPriority<T> extends AbstractBuiltInAction<T> {

    private final List<Pair<Requirement<T>[], Action<T>[]>> conditionActionPairList;

    public ActionPriority(
            BukkitCustomCropsPlugin plugin,
            AbstractActionManager<T> manager,
            Class<T> tClass,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.conditionActionPairList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            if (entry.getValue() instanceof Section inner) {
                Action<T>[] actions = manager.parseActions(inner.getSection("actions"));
                Requirement<T>[] requirements = plugin.getRequirementManager(tClass).parseRequirements(inner.getSection("conditions"), false);
                conditionActionPairList.add(Pair.of(requirements, actions));
            }
        }
    }

    @Override
    protected void triggerAction(Context<T> context) {
        outer:
        for (Pair<Requirement<T>[], Action<T>[]> pair : conditionActionPairList) {
            if (pair.left() != null)
                for (Requirement<T> requirement : pair.left()) {
                    if (!requirement.isSatisfied(context)) {
                        continue outer;
                    }
                }
            if (pair.right() != null)
                for (Action<T> action : pair.right()) {
                    action.trigger(context);
                }
            return;
        }
    }

    public List<Pair<Requirement<T>[], Action<T>[]>> conditionalActions() {
        return conditionActionPairList;
    }
}
