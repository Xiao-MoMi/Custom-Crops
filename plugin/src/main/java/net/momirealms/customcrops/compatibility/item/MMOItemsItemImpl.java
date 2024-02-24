/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.compatibility.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.momirealms.customcrops.api.integration.ItemLibrary;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MMOItemsItemImpl implements ItemLibrary {

    @Override
    public String identification() {
        return "MMOItems";
    }

    @Override
    public ItemStack buildItem(Player player, String id) {
        String[] split = id.split(":");
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(split[0]), split[1].toUpperCase(Locale.ENGLISH));
        return mmoItem == null ? new ItemStack(Material.AIR) : mmoItem.newBuilder().build();
    }

    @Override
    public String getItemID(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        if (!nbtItem.hasTag("MMOITEMS_ITEM_ID")) return null;
        return nbtItem.getString("MMOITEMS_ITEM_ID");
    }
}
