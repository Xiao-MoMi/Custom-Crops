package net.momirealms.customcrops.api.mechanic.requirement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class State {

    private final Player player;
    private final ItemStack itemInHand;
    private final Location location;
    private final HashMap<String, String> args;

    public State(Player player, ItemStack itemInHand, Location location) {
        this.player = player;
        this.itemInHand = itemInHand;
        this.location = location;
        this.args = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArg(String key, String value) {
        args.put(key, value);
    }
}
