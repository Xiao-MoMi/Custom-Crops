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
import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.misc.value.MathValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ActionDropItemLegacy<T> extends AbstractBuiltInAction<T> {

    private final List<Action<T>> actions;

    public ActionDropItemLegacy(
            BukkitCustomCropsPlugin plugin,
            ActionManager<T> manager,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.actions = new ArrayList<>();
        Section otherItemSection = section.getSection("other-items");
        if (otherItemSection != null) {
            for (Map.Entry<String, Object> entry : otherItemSection.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section inner) {
                    actions.add(requireNonNull(manager.getActionFactory("drop-item")).process(inner, section.contains("chance") ? MathValue.auto(section.get("chance")) : MathValue.plain(1d)));
                }
            }
        }
        Section qualitySection = section.getSection("quality-crops");
        if (qualitySection != null) {
            actions.add(requireNonNull(manager.getActionFactory("quality-crops")).process(qualitySection, MathValue.plain(1)));
        }
    }

    @Override
    protected void triggerAction(Context<T> context) {
        for (Action<T> action : actions) {
            action.trigger(context);
        }
    }

    public List<Action<T>> getActions() {
        return actions;
    }
}
