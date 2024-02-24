package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlaceFurnitureWrapper extends InteractWrapper {

    private final Location location;
    private final String id;

    public PlaceFurnitureWrapper(Player player, ItemStack itemInHand, Location location, String id) {
        super(player, itemInHand);
        this.location = location;
        this.id = id;
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
