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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionChain<T> extends AbstractBuiltInAction<T> {

    private final List<Action<T>> actions;

    public ActionChain(
            BukkitCustomCropsPlugin plugin,
            AbstractActionManager<T> manager,
            Object args,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.actions = new ArrayList<>();
        if (args instanceof Section section) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section innerSection) {
                    actions.add(manager.parseAction(innerSection));
                }
            }
        }
    }

    @Override
    protected void triggerAction(Context<T> context) {
        for (Action<T> action : actions) {
            action.trigger(context);
        }
    }

    public List<Action<T>> actions() {
        return actions;
    }
}
