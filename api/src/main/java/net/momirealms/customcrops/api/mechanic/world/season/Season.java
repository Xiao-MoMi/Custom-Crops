package net.momirealms.customcrops.api.mechanic.world.season;

public enum Season {

    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    public Season getNextSeason() {
        return Season.values()[(this.ordinal() + 1) % 4];
    }
}