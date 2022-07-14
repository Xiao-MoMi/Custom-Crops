package net.momirealms.customcrops.fertilizer;

public class SpeedGrow implements Fertilizer{

    private double chance;
    private String key;
    private int times;
    private final boolean before;
    private String name;

    public SpeedGrow(String key, int times, double chance, boolean before){
        this.chance = chance;
        this.times = times;
        this.before = before;
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public int getTimes() {
        return this.times;
    }

    @Override
    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public boolean isBefore() {
        return this.before;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
