package net.momirealms.customcrops.api.object.fertilizer;

import net.momirealms.customcrops.CustomCrops;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class Fertilizer implements Serializable {

    private final String key;
    private int times;

    public Fertilizer(String key, int times) {
        this.key = key;
        this.times = times;
    }

    public Fertilizer(FertilizerConfig fertilizerConfig) {
        this.key = fertilizerConfig.getKey();
        this.times = fertilizerConfig.getTimes();
    }

    /*
    If fertilizer is used up
     */
    public boolean reduceTimes() {
        times--;
        return times <= 0;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public FertilizerConfig getConfig() {
        return CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(this);
    }
}
