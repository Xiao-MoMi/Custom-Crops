package net.momirealms.customcrops.fertilizer;

public interface Fertilizer {
    String getKey();
    int getTimes();
    void setTimes(int times);
    boolean isBefore();
    String getName();
}
