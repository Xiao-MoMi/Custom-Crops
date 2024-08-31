package net.momirealms.customcrops.api.core.block;

public enum BreakReason {
    // Crop was broken by a player
    BREAK,
    // Crop was trampled
    TRAMPLE,
    // Crop was broken due to an explosion (block or entity)
    EXPLODE,
    // Crop was broken due to a specific action
    ACTION
}
