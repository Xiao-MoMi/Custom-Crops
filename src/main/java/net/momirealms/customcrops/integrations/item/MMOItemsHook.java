package net.momirealms.customcrops.integrations.item;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

public class MMOItemsHook {

    public static ItemStack get(String id) {
        id = id.substring(9);
        String[] split = StringUtils.split(id, ":");
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(split[0]), split[1]);
        return mmoItem == null ? null : mmoItem.newBuilder().build();
    }
}
