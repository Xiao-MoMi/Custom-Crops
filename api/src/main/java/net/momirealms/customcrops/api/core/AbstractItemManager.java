package net.momirealms.customcrops.api.core;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemManager implements ItemManager {

    public abstract void handlePlayerInteractAir(
            Player player,
            EquipmentSlot hand,
            ItemStack itemInHand
    );

    public abstract void handlePlayerInteractBlock(
            Player player,
            Block block,
            String blockID,
            BlockFace blockFace,
            EquipmentSlot hand,
            ItemStack itemInHand,
            Cancellable event
    );

    // it's not a good choice to use Entity as parameter because the entity might be fake
    public abstract void handlePlayerInteractFurniture(
            Player player,
            Location location,
            String furnitureID,
            EquipmentSlot hand,
            ItemStack itemInHand,
            Cancellable event
    );

    public abstract void handlePlayerBreak(
            Player player,
            Location location,
            ItemStack itemInHand,
            String brokenID,
            Cancellable event
    );

    public abstract void handlePlayerPlace(
            Player player,
            Location location,
            String placedID,
            EquipmentSlot hand,
            ItemStack itemInHand,
            Cancellable event
    );
}
