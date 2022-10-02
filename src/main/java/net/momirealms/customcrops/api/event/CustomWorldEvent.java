package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.objects.WorldState;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

public class CustomWorldEvent extends WorldEvent {

    private static final HandlerList handlers = new HandlerList();
    private final WorldState state;

    public CustomWorldEvent(@NotNull World world, WorldState worldState) {
        super(world);
        this.state = worldState;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public WorldState getState() {
        return state;
    }
}
