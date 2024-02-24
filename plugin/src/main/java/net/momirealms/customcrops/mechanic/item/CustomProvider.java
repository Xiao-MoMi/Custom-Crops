package net.momirealms.customcrops.mechanic.item;

import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface CustomProvider {

    void removeBlock(Location location);

    void placeCustomBlock(Location location, String id);

    default void placeBlock(Location location, String id) {
        if (ConfigUtils.isVanillaItem(id)) {
            location.getBlock().setType(Material.valueOf(id));
        } else {
            placeCustomBlock(location, id);
        }
    }

    void placeFurniture(Location location, String id);

    String getBlockID(Block block);

    String getItemID(ItemStack itemInHand);

    ItemStack getItemStack(String id);

    String getEntityID(Entity entity);

    default boolean isAir(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR)
            return false;
        Location center = location.toCenterLocation();
        Collection<Entity> entities = center.getWorld().getNearbyEntities(center, 0.5,0.51,0.5);
        entities.removeIf(entity -> entity instanceof Player);
        return entities.size() == 0;
    }
}
