package net.momirealms.customcrops.api.object.hologram;

public class TextDisplayMeta {

    private final boolean hasShadow;
    private final boolean isSeeThrough;
    private final boolean useDefaultBackground;
    private final int backgroundColor;
    private final byte opacity;

    public TextDisplayMeta(boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackground, int backgroundColor, byte opacity) {
        this.hasShadow = hasShadow;
        this.isSeeThrough = isSeeThrough;
        this.useDefaultBackground = useDefaultBackground;
        this.backgroundColor = backgroundColor;
        this.opacity = opacity;
    }

    public boolean isHasShadow() {
        return hasShadow;
    }

    public boolean isSeeThrough() {
        return isSeeThrough;
    }

    public boolean isUseDefaultBackground() {
        return useDefaultBackground;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public byte getOpacity() {
        return opacity;
    }
}
