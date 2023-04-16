package net.momirealms.customcrops.integration.item;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.util.ConfigUtils;
import net.momirealms.customcrops.integration.ItemInterface;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DefaultImpl implements ItemInterface {

    @Nullable
    @Override
    public ItemStack build(String id) {
        if (ConfigUtils.isVanillaItem(id)) {
            return new ItemStack(Material.valueOf(id));
        }
        else {
            return CustomCrops.getInstance().getPlatformInterface().getItemStack(id);
        }
    }
}
