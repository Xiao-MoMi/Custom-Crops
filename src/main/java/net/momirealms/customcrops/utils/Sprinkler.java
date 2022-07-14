package net.momirealms.customcrops.utils;

public class Sprinkler {

    private int water;
    private int range;
    private String namespacedID_1;
    private String namespacedID_2;

    public Sprinkler(int range, int water){
        this.water = water;
        this.range = range;
    }

    public int getWater() {
        return water;
    }
    public String getNamespacedID_1() {
        return namespacedID_1;
    }
    public String getNamespacedID_2() {
        return namespacedID_2;
    }
    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
    public void setNamespacedID_2(String namespacedID_2) {
        this.namespacedID_2 = namespacedID_2;
    }
    public void setNamespacedID_1(String namespacedID_1) {
        this.namespacedID_1 = namespacedID_1;
    }
    public void setWater(int water) {
        this.water = water;
    }
}
