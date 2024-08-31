package net.momirealms.customcrops.api.core;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface CustomItemProvider {

    boolean removeCustomBlock(Location location);

    boolean placeCustomBlock(Location location, String id);

    @Nullable
    Entity placeFurniture(Location location, String id);

    boolean removeFurniture(Entity entity);

    @Nullable
    String blockID(Block block);

    @Nullable
    String itemID(ItemStack itemStack);

    @Nullable
    ItemStack itemStack(Player player, String id);

    @Nullable
    String furnitureID(Entity entity);

    boolean isFurniture(Entity entity);
}
