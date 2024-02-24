package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BreakBlockWrapper extends ConditionWrapper {

    private final Player player;
    private final ItemStack itemInHand;
    private final Block brokenBlock;

    public BreakBlockWrapper(Player player, ItemStack itemInHand, Block brokenBlock) {
        this.player = player;
        this.itemInHand = itemInHand;
        this.brokenBlock = brokenBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    public Block getBrokenBlock() {
        return brokenBlock;
    }
}

