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

import net.momirealms.customcrops.api.common.Initable;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionFactory;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public interface ActionManager extends Initable {

    /**
     * Registers an ActionFactory for a specific action type.
     * This method allows you to associate an ActionFactory with a custom action type.
     *
     * @param type           The custom action type to register.
     * @param actionFactory  The ActionFactory responsible for creating actions of the specified type.
     * @return True if the registration was successful (the action type was not already registered), false otherwise.
     */
    boolean registerAction(String type, ActionFactory actionFactory);

    /**
     * Unregisters an ActionFactory for a specific action type.
     * This method allows you to remove the association between an action type and its ActionFactory.
     *
     * @param type The custom action type to unregister.
     * @return True if the action type was successfully unregistered, false if it was not found.
     */
    boolean unregisterAction(String type);

    /**
     * Retrieves an Action object based on the configuration provided in a ConfigurationSection.
     * This method reads the type of action from the section, obtains the corresponding ActionFactory,
     * and builds an Action object using the specified values and chance.
     * <p>
     * events:
     *   success:
     *     action_1:  <- section
     *       ...
     *
     * @param section The ConfigurationSection containing the action configuration.
     * @return An Action object created based on the configuration, or an EmptyAction instance if the action type is invalid.
     */
    Action getAction(ConfigurationSection section);

    /**
     * Retrieves a mapping of ActionTriggers to arrays of Actions from a ConfigurationSection.
     * This method iterates through the provided ConfigurationSection to extract action triggers
     * and their associated arrays of Actions.
     * <p>
     * events:  <- section
     *   success:
     *     action_1:
     *       ...
     *
     * @param section The ConfigurationSection containing action mappings.
     * @return A HashMap where keys are ActionTriggers and values are arrays of Action objects.
     */
    HashMap<ActionTrigger, Action[]> getActionMap(ConfigurationSection section);

    /**
     * Retrieves an array of Action objects from a ConfigurationSection.
     * This method iterates through the provided ConfigurationSection to extract Action configurations
     * and build an array of Action objects.
     * <p>
     * events:
     *   success:  <- section
     *     action_1:
     *       ...
     *
     * @param section The ConfigurationSection containing action configurations.
     * @return An array of Action objects created based on the configurations in the section.
     */
    Action[] getActions(ConfigurationSection section);

    /**
     * Retrieves an ActionFactory associated with a specific action type.
     *
     * @param type The action type for which to retrieve the ActionFactory.
     * @return The ActionFactory associated with the specified action type, or null if not found.
     */
    ActionFactory getActionFactory(String type);

    /**
     * Triggers a list of actions with the given condition.
     * If the list of actions is not null, each action in the list is triggered.
     *
     * @param actions   The list of actions to trigger.
     * @param state The condition associated with the actions.
     */
    static void triggerActions(State state, Action... actions) {
        if (actions != null)
            for (Action action : actions)
                action.trigger(state);
    }
}
