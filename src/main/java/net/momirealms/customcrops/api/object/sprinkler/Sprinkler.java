package net.momirealms.customcrops.api.object.sprinkler;

import java.io.Serializable;

public class Sprinkler implements Serializable {

    private int water;
    private int range;

    public Sprinkler(int water, int range) {
        this.water = water;
        this.range = range;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getRange() {
        return range;
    }
}
