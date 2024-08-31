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

import java.math.BigInteger;
import java.util.UUID;

/**
 * Utility class for handling operations related to UUIDs.
 * Provides methods for converting between different UUID formats and representations.
 */
public class UUIDUtils {

    /**
     * Converts a UUID string without dashes to a {@link UUID} object.
     *
     * @param id the UUID string without dashes
     * @return the corresponding {@link UUID} object, or null if the input string is null
     */
    public static UUID fromUnDashedUUID(String id) {
        return id == null ? null : new UUID(
                new BigInteger(id.substring(0, 16), 16).longValue(),
                new BigInteger(id.substring(16, 32), 16).longValue()
        );
    }

    /**
     * Converts a {@link UUID} object to a string without dashes.
     *
     * @param uuid the {@link UUID} object
     * @return the UUID string without dashes
     */
    public static String toUnDashedUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    /**
     * Converts an integer array to a {@link UUID} object.
     * The array must contain exactly four integers.
     *
     * @param array the integer array
     * @return the corresponding {@link UUID} object
     * @throws IllegalArgumentException if the array length is not four
     */
    public static UUID uuidFromIntArray(int[] array) {
        return new UUID((long)array[0] << 32 | (long)array[1] & 4294967295L, (long)array[2] << 32 | (long)array[3] & 4294967295L);
    }

    /**
     * Converts a {@link UUID} object to an integer array.
     * The resulting array contains exactly four integers.
     *
     * @param uuid the {@link UUID} object
     * @return the integer array representation of the UUID
     */
    public static int[] uuidToIntArray(UUID uuid) {
        long l = uuid.getMostSignificantBits();
        long m = uuid.getLeastSignificantBits();
        return leastMostToIntArray(l, m);
    }

    /**
     * Converts the most significant and least significant bits of a UUID to an integer array.
     *
     * @param uuidMost  the most significant bits of the UUID
     * @param uuidLeast the least significant bits of the UUID
     * @return the integer array representation of the UUID bits
     */
    private static int[] leastMostToIntArray(long uuidMost, long uuidLeast) {
        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
    }
}
