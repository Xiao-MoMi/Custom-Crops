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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for handling operations with arrays.
 */
public class ArrayUtils {

    private ArrayUtils() {}

    /**
     * Creates a subarray from the specified array starting from the given index.
     *
     * @param array the original array
     * @param index the starting index for the subarray
     * @param <T>   the type of the elements in the array
     * @return the subarray starting from the given index
     * @throws IllegalArgumentException if the index is less than 0
     */
    public static <T> T[] subArray(T[] array, int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index should be a value no lower than 0");
        }
        if (array.length <= index) {
            @SuppressWarnings("unchecked")
            T[] emptyArray = (T[]) Array.newInstance(array.getClass().getComponentType(), 0);
            return emptyArray;
        }
        @SuppressWarnings("unchecked")
        T[] subArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - index);
        System.arraycopy(array, index, subArray, 0, array.length - index);
        return subArray;
    }

    /**
     * Splits the specified array into a list of subarrays, each with the specified chunk size.
     *
     * @param array     the original array
     * @param chunkSize the size of each chunk
     * @param <T>       the type of the elements in the array
     * @return a list of subarrays
     */
    public static <T> List<T[]> splitArray(T[] array, int chunkSize) {
        List<T[]> result = new ArrayList<>();
        for (int i = 0; i < array.length; i += chunkSize) {
            int end = Math.min(array.length, i + chunkSize);
            @SuppressWarnings("unchecked")
            T[] chunk = (T[]) Array.newInstance(array.getClass().getComponentType(), end - i);
            System.arraycopy(array, i, chunk, 0, end - i);
            result.add(chunk);
        }
        return result;
    }

    /**
     * Appends an element to the specified array.
     *
     * @param array   the original array
     * @param element the element to append
     * @param <T>     the type of the elements in the array
     * @return a new array with the appended element
     */
    public static <T> T[] appendElementToArray(T[] array, T element) {
        T[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = element;
        return newArray;
    }

    /**
     * Splits a string value into an array of substrings based on comma separation.
     * The input string is expected to be in the format "[value1, value2, ...]".
     *
     * @param value the string value to split
     * @return an array of substrings
     */
    public static String[] splitValue(String value) {
        return value.substring(value.indexOf('[') + 1, value.lastIndexOf(']'))
                .replaceAll("\\s", "")
                .split(",");
    }
}
