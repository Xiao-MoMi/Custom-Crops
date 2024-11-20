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

import net.momirealms.customcrops.api.misc.value.MathValue;

/**
 * Interface representing a factory for creating actions.
 *
 * @param <T> the type of object that the action will operate on
 */
public interface ActionFactory<T> {

    /**
     * Constructs an action based on the provided arguments.
     *
     * @param args the args containing the arguments needed to build the action
     * @return the constructed action
     */
    Action<T> process(Object args, MathValue<T> chance);
}
