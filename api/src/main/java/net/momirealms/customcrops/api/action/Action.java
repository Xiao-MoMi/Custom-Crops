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

import net.momirealms.customcrops.api.context.Context;

/**
 * The Action interface defines a generic action that can be triggered based on a provided context.
 *
 * @param <T> the type of the object that is used in the context for triggering the action.
 */
public interface Action<T> {

    /**
     * Triggers the action based on the provided condition.
     *
     * @param context the context
     */
    void trigger(Context<T> context);

    static <T> Action<T> empty() {
        return EmptyAction.instance();
    }
}
