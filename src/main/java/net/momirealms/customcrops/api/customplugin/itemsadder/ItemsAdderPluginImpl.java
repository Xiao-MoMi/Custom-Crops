package net.momirealms.customcrops.api.customplugin.itemsadder;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.api.customplugin.PlatformInterface;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderPluginImpl implements PlatformInterface {

    @Override
    public boolean removeCustomBlock(Location location) {
        return CustomBlock.remove(location);
    }

    @Nullable
    @Override
    public String getCustomBlockID(Location location) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(location.getBlock());
        return customBlock == null ? null : customBlock.getNamespacedID();
    }

    @Nullable
    @Override
    public ItemStack getItemStack(String id) {
        CustomStack customStack = CustomStack.getInstance(id);
        return customStack == null ? null : customStack.getItemStack();
    }

    @Nullable
    @Override
    public ItemFrame placeItemFrame(Location location, String id) {
        CustomFurniture customFurniture = CustomFurniture.spawn(id, location.getBlock());
        Entity entity = customFurniture.getArmorstand();
        if (entity instanceof ItemFrame itemFrame)
            return itemFrame;
        else {
            customFurniture.remove(false);
        }
        return null;
    }

    @Nullable
    @Override
    public ItemDisplay placeItemDisplay(Location location, String id) {
        //TODO Not implemented
        return null;
    }

    @Override
    public void placeNoteBlock(Location location, String id) {
        CustomBlock.place(id, location);
    }

    @Override
    public void placeTripWire(Location location, String id) {
        CustomBlock.place(id, location);
    }

    @NotNull
    @Override
    public String getBlockID(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        return customBlock == null ? block.getType().name() : customBlock.getNamespacedID();
    }

    @Override
    public boolean doesItemExist(String id) {
        return CustomStack.getInstance(id) != null;
    }

    @Override
    public void dropBlockLoot(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) return;
        Location block_loc = block.getLocation();
        for (ItemStack itemStack : customBlock.getLoot()) {
            block_loc.getWorld().dropItemNaturally(block_loc, itemStack);
        }
    }

    @Override
    public boolean removeItemDisplay(Location location) {
        //TODO Not implemented
        return false;
    }

    @Override
    public void placeChorus(Location location, String id) {
        CustomBlock.place(id, location);
    }

    @Override
    public Location getItemFrameLocation(Location location) {
        return location.clone().add(0.5, 0.5, 0.5);
    }

    @Nullable
    @Override
    public String getCustomItemAt(Location location) {
        String block = getBlockID(location.getBlock());
        if (!block.equals("AIR")) return block;

        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame != null) {
            CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(itemFrame);
            if (customFurniture != null) {
                return customFurniture.getNamespacedID();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getItemID(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
            if (nbtCompound != null) return nbtCompound.getString("namespace") + ":" + nbtCompound.getString("id");
        }
        return itemStack.getType().name();
    }
}
