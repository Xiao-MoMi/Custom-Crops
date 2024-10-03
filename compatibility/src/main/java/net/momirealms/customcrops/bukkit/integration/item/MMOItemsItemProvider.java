/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.bukkit.integration.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.momirealms.customcrops.api.integration.ItemProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class MMOItemsItemProvider implements ItemProvider {

    @Override
    public String identifier() {
        return "MMOItems";
    }

    @NotNull
    @Override
    public ItemStack buildItem(@NotNull Player player, @NotNull String id) {
        String[] split = id.split(":", 2);
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(split[0]), split[1].toUpperCase(Locale.ENGLISH));
        return mmoItem == null ? new ItemStack(Material.AIR) : requireNonNull(mmoItem.newBuilder().build());
    }

    @Override
    public String itemID(@NotNull ItemStack itemStack) {
        NBTItem nbtItem = NBTItem.get(itemStack);
        if (!nbtItem.hasTag("MMOITEMS_ITEM_ID")) return null;
        if (nbtItem.hasType()) {
            return nbtItem.getType() + ":" + nbtItem.getString("MMOITEMS_ITEM_ID");
        } else {
            return nbtItem.getString("MMOITEMS_ITEM_ID");
        }
    }
}
