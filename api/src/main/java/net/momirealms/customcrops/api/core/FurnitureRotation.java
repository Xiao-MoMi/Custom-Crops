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

package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.RandomUtils;
import org.bukkit.Rotation;

public enum FurnitureRotation {

    NONE(0f),
    EAST(-90f),
    SOUTH(0f),
    WEST(90f),
    NORTH(180f);

    private final float yaw;

    FurnitureRotation(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public static FurnitureRotation random() {
        return FurnitureRotation.values()[RandomUtils.generateRandomInt(1, 4)];
    }

    public static FurnitureRotation getByRotation(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE -> {
                return FurnitureRotation.WEST;
            }
            case COUNTER_CLOCKWISE -> {
                return FurnitureRotation.EAST;
            }
            case FLIPPED -> {
                return FurnitureRotation.NORTH;
            }
            default -> {
                return FurnitureRotation.SOUTH;
            }
        }
    }

    public static FurnitureRotation getByYaw(float yaw) {
        yaw = (Math.abs(yaw + 180) % 360);
        switch ((int) (yaw/90)) {
            case 1 -> {
                return FurnitureRotation.WEST;
            }
            case 2 -> {
                return FurnitureRotation.NORTH;
            }
            case 3 -> {
                return FurnitureRotation.EAST;
            }
            default -> {
                return FurnitureRotation.SOUTH;
            }
        }
    }

    public Rotation getBukkitRotation() {
        switch (this) {
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
