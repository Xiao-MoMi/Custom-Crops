package net.momirealms.customcrops.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    private static final ThreadLocalRandom randomSource = ThreadLocalRandom.current();

    public static int getRandomInt(int from, int to) {
        return randomSource.nextInt(from, to);
    }
}
