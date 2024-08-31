package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.item.Item;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemManager {

    void place(@NotNull Location location, @NotNull ExistenceForm form, @NotNull String id, FurnitureRotation rotation);

    FurnitureRotation remove(@NotNull Location location, @NotNull ExistenceForm form);

    void placeBlock(@NotNull Location location, @NotNull String id);

    void placeFurniture(@NotNull Location location, @NotNull String id, FurnitureRotation rotation);

    void removeBlock(@NotNull Location location);

    @Nullable
    FurnitureRotation removeFurniture(@NotNull Location location);

    @NotNull
    default String blockID(@NotNull Location location) {
        return blockID(location.getBlock());
    }

    @NotNull
    String blockID(@NotNull Block block);

    @Nullable
    String furnitureID(@NotNull Entity entity);

    @NotNull String entityID(@NotNull Entity entity);

    @Nullable
    String furnitureID(Location location);

    @NotNull
    String anyID(Location location);

    @Nullable
    String id(Location location, ExistenceForm form);

    void setCustomEventListener(@NotNull AbstractCustomEventListener listener);

    void setCustomItemProvider(@NotNull CustomItemProvider provider);

    String id(ItemStack itemStack);

    @Nullable
    ItemStack build(Player player, String id);

    Item<ItemStack> wrap(ItemStack itemStack);

    void decreaseDamage(Player player, ItemStack itemStack, int amount);

    void increaseDamage(Player holder, ItemStack itemStack, int amount);
}
