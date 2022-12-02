package net.momirealms.customcrops.objects;

public class GrowingCrop {

    private int stage;

    private final String type;

    public GrowingCrop(int stage, String type) {
        this.stage = stage;
        this.type = type;
    }

    public int getStage() {
        return stage;
    }

    public String getType() {
        return type;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
