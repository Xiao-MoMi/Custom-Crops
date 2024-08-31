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

package net.momirealms.customcrops.api.requirement;

import net.momirealms.customcrops.api.context.Context;

/**
 * Interface representing a requirement that must be met.
 * This can be used to define conditions that need to be satisfied within a given context.
 *
 * @param <T> the type parameter for the context
 */
public interface Requirement<T> {

    /**
     * Evaluates whether the requirement is met within the given context.
     *
     * @param context the context in which the requirement is evaluated
     * @return true if the requirement is met, false otherwise
     */
    boolean isSatisfied(Context<T> context);

    static <T> Requirement<T> empty() {
        return EmptyRequirement.instance();
    }
}