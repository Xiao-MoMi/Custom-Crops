package net.momirealms.customcrops.objects.fertilizer;

import org.bukkit.Particle;

public class Fertilizer {

    String key;
    int times;
    boolean before;
    String name;
    Particle particle;

    protected Fertilizer(String key, int times) {
        this.key = key;
        this.times = times;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }
}
