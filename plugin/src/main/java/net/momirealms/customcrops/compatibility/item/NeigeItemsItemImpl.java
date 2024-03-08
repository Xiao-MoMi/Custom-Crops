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

import net.momirealms.customcrops.api.integration.ItemLibrary;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;
import pers.neige.neigeitems.utils.ItemUtils;

public class NeigeItemsItemImpl implements ItemLibrary {

    @Override
    public String identification() {
        return "NeigeItems";
    }

    @Override
    public ItemStack buildItem(Player player, String id) {
        return ItemManager.INSTANCE.getItemStack(id, player);
    }

    @Override
    public String getItemID(ItemStack itemStack) {
        ItemInfo itemInfo = ItemUtils.isNiItem(itemStack);
        if (itemInfo != null) {
            return itemInfo.getId();
        }
        return null;
    }
}
