package net.momirealms.customcrops.api.object.pot;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Pot implements Serializable {

    private Fertilizer fertilizer;
    private int water;
    private final String key;

    public Pot(String key, Fertilizer fertilizer, int water) {
        this.key = key;
        this.fertilizer = fertilizer;
        this.water = water;
    }

    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public int getWater() {
        return water;
    }

    /*
    whether to change block model
     */
    public boolean addWater(int amount) {
        if (water == 0) {
            this.water = Math.min(getConfig().getMax_storage(), amount);
            return true;
        }
        else {
            this.water = Math.min(getConfig().getMax_storage(), water + amount);
            return false;
        }
    }

    public void setWater(int amount) {
        this.water = amount;
    }

    /*
    whether to change block model
     */
    public boolean reduceWater() {
        water--;
        return water <= 0;
    }

    /*
    whether to change block model
     */
    public boolean reduceFertilizer() {
        if (this.fertilizer != null && fertilizer.reduceTimes()) {
            this.fertilizer = null;
            return true;
        }
        return false;
    }

    public boolean isWet() {
        return water != 0;
    }

    @NotNull
    public String getPotKey() {
        return key;
    }

    public PotConfig getConfig() {
        return CustomCrops.getInstance().getPotManager().getPotConfig(key);
    }
}