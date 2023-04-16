package net.momirealms.customcrops.api.object.crop;

import net.momirealms.customcrops.CustomCrops;

import java.io.Serializable;

public class GrowingCrop implements Serializable {

    private int points;
    private final String crop;

    public GrowingCrop(String crop, int points) {
        this.points = points;
        this.crop = crop;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCropKey() {
        return crop;
    }

    public CropConfig getConfig() {
        return CustomCrops.getInstance().getCropManager().getCropConfigByID(crop);
    }
}
