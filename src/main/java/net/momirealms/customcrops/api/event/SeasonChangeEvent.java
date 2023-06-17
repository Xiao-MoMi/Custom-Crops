package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.object.season.CCSeason;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

public class SeasonChangeEvent extends WorldEvent {

    private static final HandlerList handlers = new HandlerList();
    private final CCSeason ccSeason;

    public SeasonChangeEvent(@NotNull World world, CCSeason ccSeason) {
        super(world);
        this.ccSeason = ccSeason;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public CCSeason getCcSeason() {
        return ccSeason;
    }
}
