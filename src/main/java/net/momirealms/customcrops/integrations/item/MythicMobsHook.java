package net.momirealms.customcrops.integrations.item;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MythicMobsHook {

    private static ItemExecutor itemManager;

    @Nullable
    public static ItemStack get(String id) {
        id = id.substring(11);
        if (itemManager == null) {
            itemManager = MythicBukkit.inst().getItemManager();
        }
        return itemManager.getItemStack(id);
    }
}
