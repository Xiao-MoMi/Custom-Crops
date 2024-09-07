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

package net.momirealms.customcrops.common.util;

import java.util.List;

/**
 * Utility class for handling operations related to lists.
 */
public class ListUtils {

    private ListUtils() {
    }

    /**
     * Converts an object to a list of strings.
     * If the object is a string, it returns a list containing the string.
     * If the object is a list, it casts and returns the list as a list of strings.
     *
     * @param obj the object to convert
     * @return the resulting list of strings
     * @throws IllegalArgumentException if the object cannot be converted to a list of strings
     */
    @SuppressWarnings("unchecked")
    public static List<String> toList(final Object obj) {
        if (obj == null) {
            return List.of();
        }
        if (obj instanceof String s) {
            return List.of(s);
        } else if (obj instanceof List<?> list) {
            return (List<String>) list;
        } else {
            return List.of();
        }
    }
}
