package net.momirealms.customcrops.api.util;

import org.bukkit.Rotation;

import java.util.Random;

public class RotationUtils {

    private static final Rotation[] rotations4 = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE};

    public static Rotation getRandomRotation() {
        return rotations4[new Random().nextInt(rotations4.length-1)];
    }
}
