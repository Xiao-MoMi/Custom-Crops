package net.momirealms.customcrops.api.mechanic.misc;

import org.bukkit.Rotation;

public enum CRotation {

    NONE(0f),
    RANDOM(0f),
    EAST(-90f),
    SOUTH(0f),
    WEST(90f),
    NORTH(180f);

    private final float yaw;

    CRotation(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public static CRotation getByRotation(Rotation rotation) {
        switch (rotation) {
            default -> {
                return CRotation.NONE;
            }
            case CLOCKWISE -> {
                return CRotation.WEST;
            }
            case COUNTER_CLOCKWISE -> {
                return CRotation.EAST;
            }
            case FLIPPED -> {
                return CRotation.NORTH;
            }
        }
    }

    public static CRotation getByYaw(float yaw) {
        yaw = (Math.abs(yaw + 180) % 360);
        switch ((int) (yaw/90)) {
            case 1 -> {
                return CRotation.WEST;
            }
            case 2 -> {
                return CRotation.NORTH;
            }
            case 3 -> {
                return CRotation.EAST;
            }
            default -> {
                return CRotation.SOUTH;
            }
        }
    }
}
