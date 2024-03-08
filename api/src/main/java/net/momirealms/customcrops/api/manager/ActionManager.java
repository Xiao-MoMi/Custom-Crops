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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionFactory;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public interface ActionManager extends Reloadable {

    boolean registerAction(String type, ActionFactory actionFactory);

    boolean unregisterAction(String type);

    Action getAction(ConfigurationSection section);

    HashMap<ActionTrigger, Action[]> getActionMap(ConfigurationSection section);

    Action[] getActions(ConfigurationSection section);

    ActionFactory getActionFactory(String type);

    static void triggerActions(State state, Action... actions) {
        if (actions != null)
            for (Action action : actions)
                action.trigger(state);
    }
}
