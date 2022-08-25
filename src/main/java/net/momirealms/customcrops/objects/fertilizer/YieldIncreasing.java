package net.momirealms.customcrops.objects.fertilizer;

public class YieldIncreasing extends Fertilizer {

    private int bonus;
    private double chance;

    public YieldIncreasing(String key, int times) {
        super(key, times);
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
