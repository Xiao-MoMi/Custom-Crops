package net.momirealms.customcrops.utils;

public class WateringCan {

    private final int max;
    private final int width;
    private final int length;

    public WateringCan(int max, int width, int length){
        this.length = length;
        this.max = max;
        this.width = width;
    }

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
