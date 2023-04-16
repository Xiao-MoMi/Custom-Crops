package net.momirealms.customcrops.api.customplugin;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PlatformInterface {

    boolean removeCustomBlock(Location location);

    @Nullable
    String getCustomBlockID(Location location);

    @Nullable
    ItemStack getItemStack(String id);

    @Nullable
    ItemFrame placeItemFrame(Location location, String id);

    @Nullable
    ItemDisplay placeItemDisplay(Location location, String id);

    void placeNoteBlock(Location location, String id);

    void placeTripWire(Location location, String id);

    @NotNull
    String getBlockID(Block block);

    boolean doesItemExist(String id);

    void dropBlockLoot(Block block);

    boolean removeItemDisplay(Location location);

    void placeChorus(Location location, String id);

    Location getItemFrameLocation(Location location);

    @Nullable
    String getCustomItemAt(Location location);

    default void removeCustomItemAt(Location location) {
        removeCustomBlock(location);
        removeItemFrame(location);
    }

    @NotNull
    String getItemID(@NotNull ItemStack itemStack);

    @Nullable
    default ItemFrame getItemFrameAt(Location location) {
        Collection<ItemFrame> itemFrames = getItemFrameLocation(location).getNearbyEntitiesByType(ItemFrame.class, 0, 0, 0);
        int i = itemFrames.size();
        int j = 1;
        for (ItemFrame itemFrame : itemFrames) {
            if (j != i) {
                // To prevent item frames stack in one block
                itemFrame.remove();
                j++;
            }
            else return itemFrame;
        }
        return null;
    }

    default boolean removeItemFrame(Location location) {
        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame != null) {
            itemFrame.remove();
            return true;
        }
        return false;
    }

    default boolean detectAnyThing(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) return true;
        Collection<Entity> entities = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0.5, 0.5);
        return entities.size() != 0 || (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R3() && detectItemDisplay(location));
    }

    default boolean detectItemDisplay(Location location) {
        Collection<Entity> entities = location.clone().add(0.5,0.5,0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.5, 0.5, 0.5);
        return entities.size() != 0;
    }
}
