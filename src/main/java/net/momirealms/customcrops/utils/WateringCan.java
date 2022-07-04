package net.momirealms.customcrops.utils;

public record WateringCan(int max, int width, int length) {

    public int getMax() {
        return max;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }
}
