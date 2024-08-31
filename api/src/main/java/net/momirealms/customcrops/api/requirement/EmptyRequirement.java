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
 * Represents an empty requirement that always returns true when checking conditions.
 */
public class EmptyRequirement<T> implements Requirement<T> {

    public static <T> EmptyRequirement<T> instance() {
        return new EmptyRequirement<>();
    }

    @Override
    public boolean isSatisfied(Context<T> context) {
        return true;
    }
}
