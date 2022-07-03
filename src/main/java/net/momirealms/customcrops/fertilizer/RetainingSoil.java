package net.momirealms.customcrops.fertilizer;

public class RetainingSoil implements Fertilizer{

    private double chance;
    private String key;
    private int times;
    private boolean before;
    public String name;

    public RetainingSoil(String key, int times, double chance, boolean before){
        this.times = times;
        this.chance = chance;
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

    public void setChance(double chance) {
        this.chance = chance;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getChance() {
        return chance;
    }
}
