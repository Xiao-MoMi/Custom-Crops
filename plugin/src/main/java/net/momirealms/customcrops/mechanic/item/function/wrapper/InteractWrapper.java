package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InteractWrapper extends ConditionWrapper {

    private final Player player;
    private final ItemStack itemInHand;

    public InteractWrapper(Player player, ItemStack itemInHand) {
        this.player = player;
        this.itemInHand = itemInHand;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }
}
