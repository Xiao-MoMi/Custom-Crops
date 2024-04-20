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

    /**
     * Register a custom action type
     *
     * @param type type
     * @param actionFactory action factory
     * @return success or not
     */
    boolean registerAction(String type, ActionFactory actionFactory);

    /**
     * Unregister an action type by id
     *
     * @param type type
     * @return success or not
     */
    boolean unregisterAction(String type);

    /**
     * Build an action instance with Bukkit configs
     *
     * @param section bukkit config
     * @return action
     */
    Action getAction(ConfigurationSection section);

    /**
     * If an action type exists
     *
     * @param type type
     * @return exist or not
     */
    default boolean hasAction(String type) {
        return getActionFactory(type) != null;
    }

    /**
     * Build an action map with Bukkit configs
     *
     * @param section bukkit config
     * @return action map
     */
    HashMap<ActionTrigger, Action[]> getActionMap(ConfigurationSection section);

    /**
     * Build actions with Bukkit configs
     *
     * @param section bukkit config
     * @return actions
     */
    Action[] getActions(ConfigurationSection section);

    /**
     * Get an action factory by type
     *
     * @param type type
     * @return action factory
     */
    ActionFactory getActionFactory(String type);

    /**
     * Trigger actions
     *
     * @param state state
     * @param actions actions
     */
    static void triggerActions(State state, Action... actions) {
        if (actions != null)
            for (Action action : actions)
                action.trigger(state);
    }
}
