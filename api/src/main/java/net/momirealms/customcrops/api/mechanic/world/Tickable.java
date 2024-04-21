package net.momirealms.customcrops.api.mechanic.world;

public interface Tickable {

    /**
     * Tick
     *
     * @param interval interval
     * @param offline offline tick
     */
    void tick(int interval, boolean offline);
}
