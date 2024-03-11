package net.momirealms.customcrops.api.mechanic.misc;

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
}
