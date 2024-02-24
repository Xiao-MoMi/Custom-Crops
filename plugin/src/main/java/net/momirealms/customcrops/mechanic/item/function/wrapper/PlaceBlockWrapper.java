package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceBlockWrapper extends ConditionWrapper {

    private final Player player;
    private final ItemStack itemInHand;
    private final Block placedBlock;
    private final String blockID;

    public PlaceBlockWrapper(Player player, ItemStack itemInHand, Block placedBlock, String blockID) {
        this.player = player;
        this.itemInHand = itemInHand;
        this.blockID = blockID;
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

    public String getBlockID() {
        return blockID;
    }
}

