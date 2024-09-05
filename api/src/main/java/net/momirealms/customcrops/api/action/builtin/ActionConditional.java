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
import net.momirealms.customcrops.api.requirement.Requirement;

public class ActionConditional<T> extends AbstractBuiltInAction<T> {
    final Action<T>[] actions;
    final Requirement<T>[] requirements;
    public ActionConditional(
            BukkitCustomCropsPlugin plugin,
            AbstractActionManager<T> manager,
            Class<T> tClass,
            Section section,
            double chance
    ) {
        super(plugin, chance);
        this.actions = manager.parseActions(section.getSection("actions"));
        this.requirements = plugin.getRequirementManager(tClass).parseRequirements(section.getSection("conditions"), true);
    }
    @Override
    public void trigger(Context<T> condition) {
        if (!checkChance()) return;
        for (Requirement<T> requirement : requirements) {
            if (!requirement.isSatisfied(condition)) {
                return;
            }
        }
        for (Action<T> action : actions) {
            action.trigger(condition);
        }
    }

    public Action<T>[] getActions() {
        return actions;
    }

    public Requirement<T>[] getRequirements() {
        return requirements;
    }
}
