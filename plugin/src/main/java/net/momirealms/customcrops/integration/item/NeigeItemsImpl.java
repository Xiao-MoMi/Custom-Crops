package net.momirealms.customcrops.integration.item;

import net.momirealms.customcrops.integration.ItemInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.neige.neigeitems.manager.ItemManager;

public class NeigeItemsImpl implements ItemInterface {

    @Override
    public @Nullable ItemStack build(String material, Player player) {
        if (!material.startsWith("NeigeItems:")) return null;
        material = material.substring(11);
        return ItemManager.INSTANCE.getItemStack(material, player);
    }
}
