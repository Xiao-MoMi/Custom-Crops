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

package net.momirealms.customcrops.api.action;

/**
 * Abstract class representing an expansion of an action.
 * This class should be extended to provide specific implementations of actions.
 *
 * @param <T> the type parameter for the action factory
 */
public abstract class ActionExpansion<T> {

    /**
     * Retrieves the version of this action expansion.
     *
     * @return a String representing the version of the action expansion
     */
    public abstract String getVersion();

    /**
     * Retrieves the author of this action expansion.
     *
     * @return a String representing the author of the action expansion
     */
    public abstract String getAuthor();

    /**
     * Retrieves the type of this action.
     *
     * @return a String representing the type of action
     */
    public abstract String getActionType();

    /**
     * Retrieves the action factory associated with this action expansion.
     *
     * @return an ActionFactory of type T that creates instances of the action
     */
    public abstract ActionFactory<T> getActionFactory();
}
