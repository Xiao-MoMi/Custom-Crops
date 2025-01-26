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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating random values.
 */
public class RandomUtils {

    private final Random random;

    private RandomUtils() {
        random = ThreadLocalRandom.current();
    }

    /**
     * Static inner class to hold the singleton instance of RandomUtils.
     */
    private static class SingletonHolder {
        private static final RandomUtils INSTANCE = new RandomUtils();
    }

    /**
     * Retrieves the singleton instance of RandomUtils.
     *
     * @return the singleton instance
     */
    private static RandomUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Generates a random integer between the specified minimum and maximum values (inclusive).
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return a random integer between min and max (inclusive)
     */
    public static int generateRandomInt(int min, int max) {
        return getInstance().random.nextInt(max - min + 1) + min;
    }

    /**
     * Generates a random double between the specified minimum and maximum values (inclusive).
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return a random double between min and max (inclusive)
     */
    public static double generateRandomDouble(double min, double max) {
        return min + (max - min) * getInstance().random.nextDouble();
    }

    /**
     * Generates a random float between the specified minimum and maximum values (inclusive).
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return a random float between min and max (inclusive)
     */
    public static float generateRandomFloat(float min, float max) {
        return min + (max - min) * getInstance().random.nextFloat();
    }

    /**
     * Generates a random boolean value.
     *
     * @return a random boolean value
     */
    public static boolean generateRandomBoolean() {
        return getInstance().random.nextBoolean();
    }

    /**
     * Selects a random element from the specified array.
     *
     * @param array the array to select a random element from
     * @param <T>   the type of the elements in the array
     * @return a random element from the array
     */
    public static <T> T getRandomElementFromArray(T[] array) {
        int index = getInstance().random.nextInt(array.length);
        return array[index];
    }

    /**
     * Generates a random value based on a triangular distribution.
     *
     * @param mode      the mode (peak) of the distribution
     * @param deviation the deviation from the mode
     * @return a random value based on a triangular distribution
     */
    public static double triangle(double mode, double deviation) {
        return mode + deviation * (generateRandomDouble(0,1) - generateRandomDouble(0,1));
    }

    /**
     * Selects a specified number of random elements from the given array.
     *
     * @param array the array to select random elements from
     * @param count the number of random elements to select
     * @param <T>   the type of the elements in the array
     * @return an array containing the selected random elements
     * @throws IllegalArgumentException if the count is greater than the array length
     */
    public static <T> T[] getRandomElementsFromArray(T[] array, int count) {
        if (count > array.length) {
            throw new IllegalArgumentException("Count cannot be greater than array length");
        }

        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[count];

        for (int i = 0; i < count; i++) {
            int index = getInstance().random.nextInt(array.length - i);
            result[i] = array[index];
            array[index] = array[array.length - i - 1];
        }

        return result;
    }
}