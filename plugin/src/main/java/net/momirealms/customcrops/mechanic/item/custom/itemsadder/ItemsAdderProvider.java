package net.momirealms.customcrops.mechanic.item.custom.itemsadder;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.mechanic.item.CustomProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderProvider implements CustomProvider {

    @Override
    public void removeBlock(Location location) {
        if (!CustomBlock.remove(location)) {
            location.getBlock().setType(Material.AIR);
        }
    }

    @Override
    public void placeCustomBlock(Location location, String id) {
        CustomBlock.place(id, location);
    }

    @Override
    public void placeFurniture(Location location, String id) {
        CustomFurniture.spawnPreciseNonSolid(id, location);
    }

    @Override
    public String getBlockID(Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) {
            return block.getType().name();
        }
        return customBlock.getNamespacedID();
    }

    @Override
    public String getItemID(ItemStack itemInHand) {
        CustomStack customStack = CustomStack.byItemStack(itemInHand);
        if (customStack == null) {
            return itemInHand.getType().name();
        }
        return customStack.getNamespacedID();
    }

    @Override
    public ItemStack getItemStack(String id) {
        CustomStack customStack = CustomStack.getInstance(id);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }

    @Override
    public String getEntityID(Entity entity) {
        CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(entity);
        if (customFurniture == null) {
            return entity.getType().name();
        }
        return customFurniture.getNamespacedID();
    }
}
