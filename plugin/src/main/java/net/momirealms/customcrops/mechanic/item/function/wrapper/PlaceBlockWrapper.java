package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceBlockWrapper extends ConditionWrapper {

    private final Player player;
    private final ItemStack itemInHand;
    private final Block placedBlock;

    public PlaceBlockWrapper(Player player, ItemStack itemInHand, Block placedBlock) {
        this.player = player;
        this.itemInHand = itemInHand;
        this.placedBlock = placedBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    public Block getPlacedBlock() {
        return placedBlock;
    }
}

