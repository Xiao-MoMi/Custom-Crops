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

package net.momirealms.customcrops.util;

import net.momirealms.customcrops.api.mechanic.misc.CRotation;
import org.bukkit.Rotation;

import java.util.Random;

public class RotationUtils {

    private static final Rotation[] rotationsI = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE};
    private static final float[] rotationsF = {0f, 90f, 180f, -90f};

    public static Rotation getRandomBukkitRotation() {
        return rotationsI[new Random().nextInt(4)];
    }

    public static float getRandomFloatRotation() {
        return rotationsF[new Random().nextInt(4)];
    }

    public static float getFloatRotation(CRotation cRotation) {
        if (cRotation == CRotation.RANDOM) {
            return getRandomFloatRotation();
        }
        return cRotation.getYaw();
    }

    public static Rotation getBukkitRotation(CRotation cRotation) {
        switch (cRotation) {
            case RANDOM -> {
                return getRandomBukkitRotation();
            }
            case EAST -> {
                return Rotation.COUNTER_CLOCKWISE;
            }
            case WEST -> {
                return Rotation.CLOCKWISE;
            }
            case NORTH -> {
                return Rotation.FLIPPED;
            }
            default -> {
                return Rotation.NONE;
            }
        }
    }
}
