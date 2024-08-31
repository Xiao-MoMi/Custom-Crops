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

import net.momirealms.customcrops.api.integration.ItemProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;
import pers.neige.neigeitems.utils.ItemUtils;

import java.util.Objects;

public class NeigeItemsItemProvider implements ItemProvider {

    @Override
    public String identifier() {
        return "NeigeItems";
    }

    @NotNull
    @Override
    public ItemStack buildItem(@NotNull Player player, @NotNull String id) {
        return Objects.requireNonNull(ItemManager.INSTANCE.getItemStack(id, player));
    }

    @Override
    public String itemID(@NotNull ItemStack itemStack) {
        ItemInfo itemInfo = ItemUtils.isNiItem(itemStack);
        if (itemInfo != null) {
            return itemInfo.getId();
        }
        return null;
    }
}
