package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractFurnitureWrapper extends InteractWrapper {

    private final Location location;
    private final String id;
    private final Entity entity;

    public InteractFurnitureWrapper(Player player, ItemStack itemInHand, Location location, String id, @Nullable Entity baseEntity) {
        super(player, itemInHand);
        this.entity = baseEntity;
        this.location = location;
        this.id = id;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public String getID() {
        return id;
    }
}
