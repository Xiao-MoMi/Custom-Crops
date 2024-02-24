package net.momirealms.customcrops.api.mechanic.misc.image;

public record WaterBar(String left, String empty, String full, String right) {

    public static WaterBar of(String left, String empty, String full, String right) {
        return new WaterBar(left, empty, full, right);
    }

    public String getWaterBar(int current, int max) {
        return left +
                String.valueOf(full).repeat(current) +
                String.valueOf(empty).repeat(Math.max(max - current, 0)) +
                right;
    }
}
