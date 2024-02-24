package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InteractBlockWrapper extends InteractWrapper {

    private final Block clickedBlock;
    private final BlockFace clickedFace;

    public InteractBlockWrapper(Player player, ItemStack itemInHand, Block clickedBlock, BlockFace clickedFace) {
        super(player, itemInHand);
        this.clickedBlock = clickedBlock;
        this.clickedFace = clickedFace;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }

    public BlockFace getClickedFace() {
        return clickedFace;
    }
}
